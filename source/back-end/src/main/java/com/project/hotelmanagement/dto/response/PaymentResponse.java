package com.project.hotelmanagement.dto.response;

import com.project.hotelmanagement.enums.PaymentMethod;
import com.project.hotelmanagement.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private int id;
    private String paymentCode;
    private Double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private Date paymentDate;
    private String note;
}
