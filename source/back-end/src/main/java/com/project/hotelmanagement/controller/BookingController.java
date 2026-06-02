package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.request.BookingRequest;
import com.project.hotelmanagement.dto.response.ApiResponse;
import com.project.hotelmanagement.dto.response.BookingResponse;

import com.project.hotelmanagement.service.impl.BookingService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/booking")
@RestController
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    ApiResponse<List<BookingResponse>> getBookings () {
        return ApiResponse.<List<BookingResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("get all bookings")
                .result(service.getBookings())
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    ApiResponse<BookingResponse> createBooking (@Valid @RequestBody BookingRequest request) {
        return ApiResponse.<BookingResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("create booking")
                .result(service.createBooking(request))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}")
    ApiResponse<BookingResponse> confirmCheckin (@PathVariable int id, @RequestBody BookingRequest request){
        return ApiResponse.<BookingResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("confirm booking")
                .result(service.updateBooking(id,request))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/cancel-booking/{id}")
    ApiResponse<?> cancelBooking (@PathVariable int id){
        return ApiResponse.builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("cancel booking successfully")
                .result(service.cancelBooking(id))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    ApiResponse<?> deleteBooking (@PathVariable int id){
        boolean deleted = service.deleteBooking(id);
        if (deleted) {
            return ApiResponse.<Boolean>builder()
                    .code(HttpStatus.OK.value())
                    .message("Delete booking successfully")
                    .result(true)
                    .build();
        } else {
            return ApiResponse.<Boolean>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Cannot delete booking. Booking may be confirmed or not found")
                    .result(false)
                    .build();
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/confirm")
    ApiResponse<BookingResponse> confirmBooking(@PathVariable int id) throws Exception {
        return ApiResponse.<BookingResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Confirm booking successfully")
                .result(service.confirmBooking(id))
                .build();
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/check-in")
    ApiResponse<BookingResponse> checkIn(@PathVariable int id) throws Exception {
        return ApiResponse.<BookingResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Check-in successfully")
                .result(service.checkInBooking(id))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/check-out")
    ApiResponse<BookingResponse> checkOut(@PathVariable int id) throws Exception {
        return ApiResponse.<BookingResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Check-out successfully")
                .result(service.checkOutBooking(id))
                .build();
    }
}
