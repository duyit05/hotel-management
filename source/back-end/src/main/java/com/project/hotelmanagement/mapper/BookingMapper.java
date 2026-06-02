package com.project.hotelmanagement.mapper;

import com.project.hotelmanagement.dto.request.BookingRequest;
import com.project.hotelmanagement.dto.response.BookingResponse;
import com.project.hotelmanagement.models.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingResponse toBookingResponse (Booking booking);
    Booking toBooking (BookingRequest request);

    void updateBooking (@MappingTarget Booking booking, BookingRequest request);
}
