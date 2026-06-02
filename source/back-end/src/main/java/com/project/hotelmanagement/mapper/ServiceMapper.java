package com.project.hotelmanagement.mapper;

import com.project.hotelmanagement.dto.request.ServiceItemRequest;
import com.project.hotelmanagement.dto.response.ServiceItemResponse;
import com.project.hotelmanagement.models.ServiceItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    ServiceItemResponse toServiceResponse (ServiceItem serviceItem);
    ServiceItem toServiceItem (ServiceItemRequest request);
    void updateServiceItem (@MappingTarget ServiceItem serviceItem, ServiceItemRequest request);
}
