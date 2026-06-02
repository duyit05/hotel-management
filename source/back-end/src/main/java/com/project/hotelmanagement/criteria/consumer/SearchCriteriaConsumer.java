package com.project.hotelmanagement.criteria.consumer;

import com.project.hotelmanagement.criteria.model.SearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;

/**
 * Consumer xử lý từng SearchCriteria để build Predicate
 * Consumer to process each SearchCriteria and build Predicate
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteriaConsumer implements Consumer<SearchCriteria> {
    /**
     * CriteriaBuilder để xây dựng điều kiện
     * CriteriaBuilder to build conditions
     */
    private CriteriaBuilder builder;

    /**
     * Root đại diện cho bảng/entity đang query
     * Root representing the table/entity being queried
     */
    private Root root;

    /**
     * Predicate gom toàn bộ các điều kiện lại với nhau
     * Predicate combining all conditions together
     */
    private Predicate predicate;

    @Override
    public void accept(SearchCriteria params) {
        // Validate field exists
        try {
            root.get(params.getKey());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Field '" + params.getKey() + "' does not exist in entity");
        }
        /*
         - Tạo điều kiện: column >= value
         - SQL: price >= 100
         */
        if (params.getOperation().equals(">")) {
            /*
               Ghép điều kiện mới vào WHERE bằng AND
               SQL: WHERE (điều kiện cũ) AND price >= 100
               predicate = builder.and(predicate, condition)

               predicate ở trong and () lưu giữ lại các condition tìm kiếm trước đó và nối thêm đk mới
             */
            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(
                    root.get(params.getKey()), // cột (price)
                    params.getValue().toString()) // giá trị 100
            );
        } else if (params.getOperation().equals("<")) {
            predicate = builder.and(predicate, builder.lessThanOrEqualTo(
                    root.get(params.getKey()), params.getValue().toString()));
        } else {
            // Xử lý trường hợp String
            if (root.get(params.getKey()).getJavaType() == String.class) {
                predicate = builder.and(predicate, 
                    builder.like(
                        builder.upper(root.get(params.getKey())), 
                        "%" + params.getValue().toString().toUpperCase() + "%"
                    ));
            } else {
                // Xử lý trường hợp equals
                predicate = builder.and(predicate,
                    builder.equal(root.get(params.getKey()), params.getValue()));
            }
        }
    }
}








