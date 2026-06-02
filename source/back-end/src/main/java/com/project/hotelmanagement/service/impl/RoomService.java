package com.project.hotelmanagement.service.impl;

import com.project.hotelmanagement.criteria.service.CriteriaSearchService;
import com.project.hotelmanagement.dto.request.RoomRequest;
import com.project.hotelmanagement.dto.response.ImageResponse;
import com.project.hotelmanagement.dto.response.PageResponse;
import com.project.hotelmanagement.dto.response.RoomResponse;
import com.project.hotelmanagement.dto.response.TypeResponse;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.exception.InvalidDataException;
import com.project.hotelmanagement.integration.MinioChannel;
import com.project.hotelmanagement.mapper.ImageMapper;
import com.project.hotelmanagement.mapper.RoomMapper;
import com.project.hotelmanagement.mapper.TypeMapper;
import com.project.hotelmanagement.models.Image;
import com.project.hotelmanagement.models.Room;
import com.project.hotelmanagement.models.Type;
import com.project.hotelmanagement.repository.RoomRepository;
import com.project.hotelmanagement.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.project.hotelmanagement.exception.ErrorCode.ROOM_NOT_FOUND;
import static com.project.hotelmanagement.exception.ErrorCode.TYPE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;
    private final TypeRepository typeRepository;
    private final MinioChannel minioChannel;
    private final RoomMapper roomMapper;
    private final ImageMapper imageMapper;
    private final CriteriaSearchService criteriaSearchService;
    private final TypeMapper typeMapper;

    
    public PageResponse<?> getRooms(int pageNo, int pageSize, Integer maxOccupancy, LocalDate checkOutDate, LocalDate checkInDate, String ...sorts) {
        int page = 0;
        if(pageNo > 0){
            page = pageNo - 1;
        }
        // xử lý sort
        List<Sort.Order> orders = new ArrayList<>();
        Set<String> typeFields = Set.of("basicPrice", "maxOccupancy", "type", "amenities");
        if(sorts != null){
            for (String sortBy: sorts){
                // key:asc|desc
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sortBy);
                if(matcher.find()){
                    String fieldName = matcher.group(1);
                    String direction = matcher.group(3);
                    // Kiểm tra fields thuộc bảng Type
                    String sortField = typeFields.contains(fieldName) ? "type." + fieldName : fieldName;
                    Sort.Order order = new Sort.Order(
                            direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                            sortField
                    );
                    orders.add(order);
                }
            }
        }
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(orders));
        Page<Room> rooms;
        LocalDate safeCheckInDate = (checkInDate != null) ? checkInDate : LocalDate.of(1900, 1, 1);
        LocalDate safeCheckOutDate = (checkOutDate != null) ? checkOutDate : LocalDate.of(2100, 12, 31);
        Integer safeMaxOccupancy = (maxOccupancy != null && maxOccupancy > 0) ? maxOccupancy : null;
        if(maxOccupancy != null || checkInDate != null || checkOutDate != null){
            rooms = roomRepository.searchRooms(safeMaxOccupancy,safeCheckOutDate, safeCheckInDate, pageable);
        }else{
            rooms = roomRepository.findAll(pageable);
        }
        List<RoomResponse> responses = new ArrayList<>();
        for (Room room : rooms.getContent()){
            RoomResponse response = roomMapper.toRoomResponse(room);
            List<ImageResponse> imageResponses = new ArrayList<>();
            if(room.getImages() != null){
                for (Image image: room.getImages()){
                    ImageResponse imageResponse = new ImageResponse();
                    imageResponse.setUrl(image.getUrl());
                    imageResponses.add(imageResponse);
                }
            }
            if(room.getType() != null){
                TypeResponse typeResponse = typeMapper.toTypeResponse(room.getType());
                response.setType(typeResponse);
            }
            response.setImages(imageResponses);
            responses.add(response);
        }
        return PageResponse.builder()
                .pageNo(page)
                .pageSize(pageSize)
                .totalPage(rooms.getTotalPages())
                .items(responses)
                .build();
    }

    
    public RoomResponse createRoom(RoomRequest request) {
        Type type = typeRepository.findById(request.getTypeId()).orElseThrow(() -> new AppException(TYPE_NOT_FOUND));

        Room room = roomMapper.toRoom(request);
        room.setType(type);
        room.setImages(handleUploadFile(request.getImages(), room));
        roomRepository.save(room);
        RoomResponse response = roomMapper.toRoomResponse(room);
//        List<ImageResponse> imageResponses = new ArrayList<>();
//
//        if(room.getImages() != null){
//            for (Image image : room.getImages()){
//                 ImageResponse imageResponse = new ImageResponse();
//                 imageResponse.setUrl(image.getUrl());
//                 imageResponses.add(imageResponse);
//            }
//        }
        response.setImages(imageMapper.toListImageResponse(room.getImages()));
        return response;
    }

    
    public Room findById(int id) {
        return roomRepository.findById(id).orElseThrow(() -> new AppException(ROOM_NOT_FOUND));
    }

    
    public boolean deleteRoom(int id) {
        try {
            Room room = findById(id);
            roomRepository.delete(room);
            log.info("delete room success");
            return true;
        } catch (InvalidDataException e) {
            log.error("error: {}", e.getCause().getMessage());
        }
        return false;
    }

    
    public RoomResponse updateRoom(int id, RoomRequest request) {
        List<Image> images = new ArrayList<>();
        Room room = findById(id);
        room.setImages(handleUploadFile(request.getImages(), room));
        roomMapper.toRoomResponse(room);
        roomRepository.save(room);

        return roomMapper.toRoomResponse(room);
    }

    private List<Image> handleUploadFile(List<MultipartFile> files, Room room) {
        if (files == null || files.isEmpty()) return new ArrayList<>();
        List<Image> images = new ArrayList<>();
        for (MultipartFile file : files) {
            String urlImg = minioChannel.update(file);
            Image image = new Image();
            image.setUrl(urlImg);
            image.setRoom(room);
            images.add(image);
        }
        return images;
    }

    
    public PageResponse<?> searchWithCriteria(int pageNo, int pageSize, String sortBy, Double basicPrice, String... search) {
        return criteriaSearchService.searchWithCriteria(pageNo, pageSize, sortBy, basicPrice, search);
    }
}
