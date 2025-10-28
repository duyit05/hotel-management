package com.project.hotelmanagement.service.impl;

import com.project.hotelmanagement.dto.request.RoomRequest;
import com.project.hotelmanagement.dto.response.RoomResponse;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.exception.InvalidDataException;
import com.project.hotelmanagement.integration.MinioChannel;
import com.project.hotelmanagement.models.Image;
import com.project.hotelmanagement.models.Room;
import com.project.hotelmanagement.models.Type;
import com.project.hotelmanagement.repository.RoomRepository;
import com.project.hotelmanagement.repository.TypeRepository;
import com.project.hotelmanagement.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.project.hotelmanagement.exception.ErrorCode.ROOM_NOT_FOUND;
import static com.project.hotelmanagement.exception.ErrorCode.TYPE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final TypeRepository typeRepository;
    private final MinioChannel minioChannel;

    @Override
    public List<RoomResponse> getRooms() {
        return roomRepository.findAll().stream().map(
                room -> RoomResponse.builder()
                        .id(room.getId())
                        .code(room.getCode())
                        .name(room.getName())
                        .address(room.getAddress())
                        .description(room.getDescription())
                        .numberRoom(room.getNumberRoom())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public RoomResponse createRoom(RoomRequest request) {

        Type type = typeRepository.findById(request.getTypeId()).orElseThrow(() -> new AppException(TYPE_NOT_FOUND));
        List<Image> images = new ArrayList<>();

        Room room = new Room();
        room.setCode(request.getCode());
        room.setName(request.getName());
        room.setDescription(request.getDescription());
        room.setNumberRoom(request.getNumberRoom());
        room.setAddress(request.getAddress());
        room.setType(type);

        if(request.getImages() != null && !request.getImages().isEmpty()){
            for(MultipartFile file : request.getImages()){
                String url = minioChannel.update(file);
                Image image = new Image();
                image.setUrl(url);
                image.setRoom(room);
                images.add(image);
            }
        }
        room.setImages(images);

        roomRepository.save(room);

        return RoomResponse.builder()
                .id(room.getId())
                .code(room.getCode())
                .name(room.getName())
                .description(room.getDescription())
                .numberRoom(room.getNumberRoom())
                .address(room.getAddress())
                .build();
    }

    private Room findById(int id) {
        return roomRepository.findById(id).orElseThrow(() -> new AppException(ROOM_NOT_FOUND));
    }

    @Override
    public boolean deleteRoom(int id) {
        try {
            Room room = findById(id);
            roomRepository.delete(room);
            log.info("delete room success");
            return true;
        } catch (InvalidDataException e) {
            log.error("error: {}", e.getCause().getMessage());
        }
        return false;
    }

    @Override
    public RoomResponse updateRoom(int id, RoomRequest request) {
        Room room = findById(id);
        room.setCode(request.getCode());
        room.setName(request.getName());
        room.setDescription(request.getDescription());
        room.setNumberRoom(request.getNumberRoom());
        room.setAddress(request.getAddress());

        roomRepository.save(room);

        return RoomResponse.builder()
                .id(room.getId())
                .code(room.getCode())
                .name(room.getName())
                .description(room.getDescription())
                .numberRoom(room.getNumberRoom())
                .address(room.getAddress())
                .build();
    }
}
