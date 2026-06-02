package com.project.hotelmanagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceItemRequest {
    @NotBlank(message = "SERVICE_ITEM_NAME_INVALID")
    private String name;

    private String description;

    @Min(value = 100000,message = "SERVICE_ITEM_PRICE_INVALID")
    private Double price;

    @Min(value = 1, message = "SERVICE_ITEM_UNIT_INVALID")
    private Integer unit;
    private Boolean isAvailable;
    private Integer categoryId;
}
