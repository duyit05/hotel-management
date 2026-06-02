package com.project.hotelmanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    @NotNull(message = "CHECK_IN_DATE_INVALID")
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private LocalDate checkInDate;

    @NotNull(message = "CHECK_OUT_DATE_INVALID")
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private LocalDate checkOutDate;

    @NotNull(message = "NUMBER_OF_GUESTS_INVALID")
    @Min(value = 1, message = "NUMBER_OF_GUESTS_INVALID")
    private Integer numberOfGuests;

    private String specialRequest;
    private String note;

    @NotNull(message = "ROOM_ID_IS_NOT_NULL")
    private Integer roomId;

    private Integer discountId;
    private String discountCode;
}
