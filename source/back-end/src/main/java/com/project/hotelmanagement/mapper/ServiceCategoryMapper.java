package com.project.hotelmanagement.mapper;

import com.project.hotelmanagement.dto.request.ServiceCategoryRequest;
import com.project.hotelmanagement.dto.response.ServiceCategoryResponse;
import com.project.hotelmanagement.models.ServiceCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ServiceCategoryMapper {
    ServiceCategoryResponse toServiceCategoryResponse (ServiceCategory serviceCategory);
    ServiceCategory toServiceCategory (ServiceCategoryRequest request);
    void updateServiceCategory (@MappingTarget ServiceCategory serviceCategory, ServiceCategoryRequest request);
}
