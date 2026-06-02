package com.project.hotelmanagement.mapper;

import com.project.hotelmanagement.dto.request.DiscountRequest;
import com.project.hotelmanagement.dto.response.DiscountResponse;
import com.project.hotelmanagement.models.Discount;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DiscountMapper {
    DiscountResponse toDiscountResponse (Discount discount);
    Discount toDiscount (DiscountRequest request);
    void updateDiscount (@MappingTarget Discount discount, DiscountRequest request);
}
