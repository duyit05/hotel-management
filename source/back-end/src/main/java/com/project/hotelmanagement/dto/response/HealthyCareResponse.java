package com.project.hotelmanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthyCareResponse {
    private int id;
    private String note;
    private Integer quantity;
    private LocalDate registerDate;
    private Double totalPrice;
    private Double unitPrice;
    private ServiceItemResponse service;
    private BookingResponse booking;
}
