package com.project.hotelmanagement.dto.response;

import com.project.hotelmanagement.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private int id;
    private String bookingCode;
    private Date checkInDate;
    private Date checkOutDate;
    private LocalDateTime actualCheckInTime;
    private LocalDateTime actualCheckOutTime;
    private Integer numberOfGuests;
    private Double totalAmount;
    private Double discountAmount;
    private BookingStatus status;
    private String specialRequest;
    private String note;
}
