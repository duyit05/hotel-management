package com.project.hotelmanagement.criteria.service;

import com.project.hotelmanagement.criteria.consumer.SearchCriteriaConsumer;
import com.project.hotelmanagement.criteria.model.SearchCriteria;
import com.project.hotelmanagement.dto.response.PageResponse;
import com.project.hotelmanagement.dto.response.RoomResponse;
import com.project.hotelmanagement.mapper.RoomMapper;
import com.project.hotelmanagement.models.Room;
import com.project.hotelmanagement.models.Type;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service sử dụng JPA Criteria API để tìm kiếm động
 * Service using JPA Criteria API for dynamic search
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CriteriaSearchService {

    @PersistenceContext
    private EntityManager entityManager;
    private final RoomMapper mapper;

    /**
     * Tìm kiếm Room với Criteria API
     * Search Room using Criteria API
     * 
     * @param pageNo Số trang (bắt đầu từ 0)
     * @param pageSize Số bản ghi mỗi trang
     * @param sortBy Chuỗi sort (VD: "id:desc", "name:asc")
     * @param basicPrice Giá tối thiểu (filter basicPrice >= value)
     * @param search Mảng các điều kiện tìm kiếm (VD: ["name:Deluxe", "code:DLX"])
     * @return PageResponse chứa danh sách RoomResponse
     */
    public PageResponse<?> searchWithCriteria(int pageNo, int pageSize, String sortBy, Double basicPrice, String... search) {
        List<SearchCriteria> criteriaList = new ArrayList<>();
        // Parse search criteria từ String[] search
        if (search != null) {
            Pattern pattern = Pattern.compile("(\\w+?)([:><])(.*)");
            for (String s : search) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    criteriaList.add(new SearchCriteria(
                        matcher.group(1), 
                        matcher.group(2), 
                        matcher.group(3)
                    ));
                }
            }
        }
        // Get rooms
        List<RoomResponse> rooms = getRooms(pageNo, pageSize, criteriaList, basicPrice, sortBy);
        Long totalElements = getTotalElements(criteriaList, basicPrice);
        // Tính số trang = ceil(totalElements / pageSize)
        int totalPage = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(totalPage)
                .items(rooms)
                .build();
    }

    private Long getTotalElements(List<SearchCriteria> criteriaList, Double basicPrice) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<Room> root = query.from(Room.class);

        Predicate predicate = criteriaBuilder.conjunction();
        // Xử lý criteriaList
        if (!criteriaList.isEmpty()) {
            SearchCriteriaConsumer criteriaConsumer = new SearchCriteriaConsumer(criteriaBuilder, root, predicate);
            criteriaList.forEach(criteriaConsumer);
            predicate = criteriaConsumer.getPredicate();
        }
        // Xử lý basicPrice (nếu có) - combine với predicate hiện tại
        if (basicPrice != null && basicPrice > 0) {
            Join<Room, Type> joinRoomType = root.join("type");
            Predicate basicPricePredicate = criteriaBuilder.ge(joinRoomType.get("basicPrice"), basicPrice);
            predicate = criteriaBuilder.and(predicate, basicPricePredicate);
        }
        query.select(criteriaBuilder.count(root));
        query.where(predicate);

        return entityManager.createQuery(query).getSingleResult();
    }


    /**
     * Lấy danh sách Room với pagination và sort
     */
    private List<RoomResponse> getRooms(int pageNo, int pageSize, List<SearchCriteria> criteriaList, Double basicPrice, String sortBy) {
        // Lấy CriteriaBuilder: công cụ để xây dựng câu query (where, and, or, >, <, like...)
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Tạo CriteriaQuery: định nghĩa kiểu dữ liệu sẽ SELECT (ở đây là Room)
        CriteriaQuery<Room> query = criteriaBuilder.createQuery(Room.class);

        // Khai báo bảng chính (FROM Room), root đại diện cho bảng Room để truy cập các cột
        Root<Room> root = query.from(Room.class);

        // Xử lý các điều kiện tìm kiếm
        // Tạo 1 điều kiện luôn đúng (WHERE 1=1) để làm điểm xuất phát
        Predicate predicate = criteriaBuilder.conjunction();

        // Bước 1: Xử lý criteriaList (các điều kiện tìm kiếm từ user)
        // Nếu có điều kiện tìm kiếm thì xử lý trước
        if (!criteriaList.isEmpty()) {
            // Tạo Consumer để ghép điều kiện từ criteriaList vào predicate
            SearchCriteriaConsumer queryConsumer = new SearchCriteriaConsumer(criteriaBuilder, root, predicate);
            // Duyệt từng điều kiện tìm kiếm và combine vào predicate
            criteriaList.forEach(queryConsumer);
            // Lấy kết quả cuối cùng của ghép điều kiện (VD: name LIKE '%Deluxe%' AND code = 'DLX')
            predicate = queryConsumer.getPredicate();
        }

        // Bước 2: Xử lý basicPrice (nếu có) - combine với predicate hiện tại
        if (basicPrice != null && basicPrice > 0) {
            // Join giữa Room và Type để truy cập cột basicPrice trong bảng Type
            Join<Room, Type> joinRoomType = root.join("type");
            // Lấy ra column cần filter: type.basicPrice >= basicPrice (giá tối thiểu)
            Predicate basicPricePredicate = criteriaBuilder.ge(joinRoomType.get("basicPrice"), basicPrice);
            // Combine predicate của criteriaList (nếu có) với predicate của basicPrice bằng AND
            // VD: (name LIKE '%Deluxe%') AND (type.basicPrice >= 500000)
            predicate = criteriaBuilder.and(predicate, basicPricePredicate);
        }
        // Áp dụng tất cả các điều kiện đã build vào WHERE clause
        query.where(predicate);

        // Xử lý sort (sắp xếp)
        if (StringUtils.hasLength(sortBy)) {
            // Parse chuỗi sort (VD: "id:desc" hoặc "name:asc")
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                // Lấy tên cột cần sort (VD: "id", "name")
                String columnName = matcher.group(1);
                try {
                    // Kiểm tra chiều sort: desc (giảm dần) hay asc (tăng dần)
                    if (matcher.group(3).equalsIgnoreCase("desc")) {
                        // Sắp xếp giảm dần theo cột (VD: ORDER BY id DESC)
                        query.orderBy(criteriaBuilder.desc(root.get(columnName)));
                    } else {
                        // Sắp xếp tăng dần theo cột (VD: ORDER BY id ASC)
                        query.orderBy(criteriaBuilder.asc(root.get(columnName)));
                    }
                } catch (IllegalArgumentException e) {
                    // Nếu cột không tồn tại thì log warning và bỏ qua sort
                    log.warn("Invalid sort column: {}", columnName);
                }
            }
        }

        // Thực thi query với pagination và trả về kết quả
        return mapper.toListResponse(
                entityManager.createQuery(query)
                        // Tính offset: bỏ qua số bản ghi = pageNo * pageSize
                        // VD: pageNo=0, pageSize=10 → offset=0 (bắt đầu từ record đầu tiên)
                        // VD: pageNo=1, pageSize=10 → offset=10 (bỏ qua 10 record đầu, lấy từ record 11)
                        .setFirstResult(pageNo * pageSize)
                        // Giới hạn số bản ghi trả về = pageSize
                        // VD: pageSize=10 → chỉ lấy 10 bản ghi
                        .setMaxResults(pageSize)
                        // Thực thi query và lấy danh sách Room
                        .getResultList()
        );
    }
}








