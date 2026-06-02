package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.request.RoomRequest;
import com.project.hotelmanagement.dto.response.ApiResponse;
import com.project.hotelmanagement.dto.response.PageResponse;
import com.project.hotelmanagement.dto.response.RoomResponse;
import com.project.hotelmanagement.service.impl.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    ApiResponse<?> getRooms(@RequestParam (defaultValue = "0", required = false) int pageNo,
                            @RequestParam (defaultValue = "10", required = false) int pageSize,
                            @RequestParam (required = false) Integer maxOccupancy,
                            @RequestParam (required = false) LocalDate checkInDate,
                            @RequestParam (required = false) LocalDate checkOutDate,
                            @RequestParam (required = false) String ...sorts) {
        return ApiResponse.<PageResponse<?>>builder()
                .code(HttpStatus.OK.value())
                .message("Get rooms")
                .result(roomService.getRooms(pageNo, pageSize, maxOccupancy, checkInDate, checkOutDate, sorts))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<RoomResponse> createRoom(@Valid @ModelAttribute RoomRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Create room")
                .result(roomService.createRoom(request))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    ApiResponse<?> deleteRoom(@PathVariable int id) {
        boolean isDeleted = roomService.deleteRoom(id);
        return ApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Delete room")
                .result(isDeleted)
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    ApiResponse<RoomResponse> updateRoom(@PathVariable int id, @Valid @RequestBody RoomRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Update room")
                .result(roomService.updateRoom(id, request))
                .build();
    }

    @GetMapping("/search-with-criteria")
    ApiResponse<?> getRooms(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "20", required = false) int pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Double basicPrice,
            @RequestParam(required = false) String ... search
    ) {
        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("search with criteria")
                .result(roomService.searchWithCriteria(pageNo,pageSize,sortBy, basicPrice, search))
                .build();
    }

}
