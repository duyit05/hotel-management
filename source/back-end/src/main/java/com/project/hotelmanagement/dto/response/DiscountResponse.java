package com.project.hotelmanagement.dto.response;

import lombok.*;

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscountResponse {
    private Integer id;
    private String code;
    private String name;
    private String description;
    private Double discountValue;
    private Double maxDiscountAmount;
    private Double minOrderAmount;
    private Date startDate;
    private Date endDate;
    private Integer usingLimit;
    private Boolean isActive;
}
