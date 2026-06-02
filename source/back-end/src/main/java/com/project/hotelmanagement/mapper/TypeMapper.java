package com.project.hotelmanagement.mapper;

import com.project.hotelmanagement.dto.request.TypeRequest;
import com.project.hotelmanagement.dto.response.TypeResponse;
import com.project.hotelmanagement.models.Room;
import com.project.hotelmanagement.models.Type;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TypeMapper {
    TypeResponse toTypeResponse (Type type);
    Type toType (TypeRequest request);

    void updateType (@MappingTarget Type type, TypeRequest request);
}
