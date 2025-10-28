package com.project.hotelmanagement.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeRequest {

    @NotBlank(message = "TYPE_INVALID")
    private String type;

    @NotBlank(message = "NAME_INVALID")
    private String name;

    @NotBlank(message = "AMENITIES_INVALID")
    private String amenities;

    @NotNull(message = "BASIC_PRICE_INVALID")
    @DecimalMin(value = "100000", message = "BASIC_PRICE_TO_LOW")
    private Double basicPrice;

    @NotNull(message = "MAX_OCCUPANCY")
    @Min(value = 2, message = "MAX_OCCUPANCY_TOO_SMALL")
    @Max(value = 8, message = "MAX_OCCUPANCY_TOO_LARGE")
    private Integer maxOccupancy;
}
