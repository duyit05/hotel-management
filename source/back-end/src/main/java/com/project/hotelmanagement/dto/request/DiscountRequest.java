package com.project.hotelmanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiscountRequest {
    @NotBlank(message = "CODE_DISCOUNT_INVALID")
    private String code;

    @NotBlank(message = "NAME_DISCOUNT_INVALID")
    private String name;

    private String description;

    @NotNull(message = "DISCOUNT_VALUE_INVALID")
    @Min(value = 5, message = "DISCOUNT_VALUE_TO_LOW")
    @Max(value = 20, message = "DISCOUNT_VALUE_TO_MAX")
    private Double discountValue;

    @NotNull(message = "MAX_DISCOUNT_AMOUNT_INVALID")
    @Min(value = 100000, message = "MAX_DISCOUNT_AMOUNT_TO_LOW")
    @Max(value = 500000, message = "MAX_DISCOUNT_AMOUNT_TO_MAX")
    private Double maxDiscountAmount;

    @NotNull(message = "MIN_ORDER_AMOUNT_INVALID")
    @Min(value = 1000000, message = "MIN_ORDER_AMOUNT_TO_LOW")
    @Max(value = 2000000, message = "MIN_ORDER_AMOUNT_TO_MAX")
    private Double minOrderAmount;

    @NotNull(message = "START_DATE_INVALID")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private Date startDate;

    @NotNull(message = "END_DATE_INVALID")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private Date endDate;

    @NotNull(message = "USING_LIMIT")
    @Min(value = 2, message = "USING_LIMIT_MIN")
    @Max(value = 5, message = "USING_LIMIT_MAX")
    private Integer usingLimit;

    private Boolean isActive;
}
