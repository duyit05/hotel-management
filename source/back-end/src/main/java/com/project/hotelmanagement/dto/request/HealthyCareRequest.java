package com.project.hotelmanagement.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthyCareRequest {
    private String note;
    private Integer quantity;
    private LocalDate registerDate;
    private Double totalPrice;
    private Double unitPrice;
    private Integer bookingId;
    private Integer serviceId;
}
