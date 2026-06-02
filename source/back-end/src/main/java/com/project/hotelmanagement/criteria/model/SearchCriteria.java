package com.project.hotelmanagement.criteria.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model chứa thông tin điều kiện tìm kiếm
 * Model containing search condition information
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    /**
     * Tên field cần tìm kiếm (VD: code, name, description)
     * Field name to search (e.g., code, name, description)
     */
    private String key;
    
    /**
     * Toán tử so sánh: >, <, : (equals/like)
     * Comparison operator: >, <, : (equals/like)
     */
    private String operation;
    
    /**
     * Giá trị cần tìm kiếm
     * Search value
     */
    private Object value;
}








