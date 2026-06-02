package com.project.hotelmanagement.mapper;

import com.project.hotelmanagement.dto.request.RoomRequest;
import com.project.hotelmanagement.dto.response.RoomResponse;
import com.project.hotelmanagement.models.Room;
import com.project.hotelmanagement.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    // target = "images" -> field trong RoomResponse
    // source = "images" -> field trong List<Image>
//    @Mapping(target = "images", ignore = true)
    RoomResponse toRoomResponse (Room room);
    Room toRoom (RoomRequest request);
    @Mapping(target = "images", ignore = true)
    void updateRoom (@MappingTarget Room room, RoomRequest request);

    List<RoomResponse> toListResponse (List<Room> rooms);
}
