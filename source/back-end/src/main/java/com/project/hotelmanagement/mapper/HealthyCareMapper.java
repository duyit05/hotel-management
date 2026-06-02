package com.project.hotelmanagement.mapper;

import com.project.hotelmanagement.dto.request.HealthyCareRequest;
import com.project.hotelmanagement.dto.request.RoomRequest;
import com.project.hotelmanagement.dto.response.HealthyCareResponse;
import com.project.hotelmanagement.models.HealthyCare;
import com.project.hotelmanagement.models.Room;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HealthyCareMapper {
    HealthyCareResponse toHealthyCareResponse (HealthyCare request);
    HealthyCare toHealthyCare (HealthyCareRequest request);

    void updateHealthyCare (@MappingTarget HealthyCare healthyCare, HealthyCareRequest request);
}
