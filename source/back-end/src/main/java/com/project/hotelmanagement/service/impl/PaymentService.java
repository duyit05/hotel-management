package com.project.hotelmanagement.service.impl;

import com.project.hotelmanagement.dto.request.PaymentRequest;
import com.project.hotelmanagement.dto.response.PaymentResponse;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.exception.InvalidDataException;
import com.project.hotelmanagement.mapper.PaymentMapper;
import com.project.hotelmanagement.models.Payment;
import com.project.hotelmanagement.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.hotelmanagement.exception.ErrorCode.PAYMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    
    public List<PaymentResponse> getPayments() {
        return paymentRepository.findAll().stream().map(paymentMapper::toPaymentResponse).toList();
    }

    
    public PaymentResponse createPayment(PaymentRequest request) {
        Payment payment = paymentMapper.toPayment(request);
        paymentRepository.save(payment);
        return paymentMapper.toPaymentResponse(payment);
    }

    private Payment findPaymentById (int id){
        return paymentRepository.findById(id).orElseThrow(() -> new AppException(PAYMENT_NOT_FOUND));
    }
    
    public PaymentResponse updatePayment(int id, PaymentRequest request) {
        Payment payment = findPaymentById(id);
        paymentMapper.updatePayment(payment,request);
        return paymentMapper.toPaymentResponse(payment);
    }

    
    public boolean deletePayment(int id) {
        try {
            Payment payment = findPaymentById(id);
            paymentRepository.delete(payment);
            log.info("delete payment success");
            return true;
        }catch (InvalidDataException e){
            log.error("error: {}", e.getMessage());
        }
        return false;
    }
}
