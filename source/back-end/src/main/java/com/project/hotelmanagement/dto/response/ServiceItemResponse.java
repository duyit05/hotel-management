package com.project.hotelmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceItemResponse {
    private String name;
    private String description;
    private String price;
    private String unit;
    private Boolean isAvailable;
}
