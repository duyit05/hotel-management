package com.project.hotelmanagement.mapper;

import com.project.hotelmanagement.dto.request.PaymentRequest;
import com.project.hotelmanagement.dto.response.PaymentResponse;
import com.project.hotelmanagement.models.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponse toPaymentResponse (Payment payment);
    Payment toPayment (PaymentRequest request);
    void updatePayment (@MappingTarget Payment payment, PaymentRequest request);
}
