package com.project.hotelmanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeResponse {
    private int id;
    private String type;
    private String name;
    private String amenities;
    private double basicPrice;
    private int maxOccupancy;
}
