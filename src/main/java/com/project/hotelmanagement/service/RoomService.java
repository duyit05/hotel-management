package com.project.hotelmanagement.service;

import com.project.hotelmanagement.dto.request.RoomRequest;
import com.project.hotelmanagement.dto.response.RoomResponse;
import com.project.hotelmanagement.models.Room;

import java.util.List;

public interface RoomService {
    List<RoomResponse> getRooms ();
    RoomResponse createRoom (RoomRequest request);
    boolean deleteRoom (int id);
    RoomResponse updateRoom (int id, RoomRequest request);
}
