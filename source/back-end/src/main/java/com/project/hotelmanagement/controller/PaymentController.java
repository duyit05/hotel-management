package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.request.PaymentRequest;
import com.project.hotelmanagement.dto.response.ApiResponse;
import com.project.hotelmanagement.dto.response.PaymentResponse;
import com.project.hotelmanagement.service.impl.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/payment")
@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping
    ApiResponse<List<PaymentResponse>> getPayments (){
        return ApiResponse.<List<PaymentResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("get all payments")
                .result(paymentService.getPayments())
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    ApiResponse<PaymentResponse> createPayment (@Valid @RequestBody PaymentRequest request){
        return ApiResponse.<PaymentResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("create payment")
                .result(paymentService.createPayment(request))
                .build();
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    ApiResponse<PaymentResponse> updatePayment (@PathVariable int id ,@RequestBody PaymentRequest request){
        return ApiResponse.<PaymentResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("update payment")
                .result(paymentService.updatePayment(id, request))
                .build();
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    ApiResponse<?> deletePayment (@PathVariable int id){
        return ApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("delete payment")
                .result(paymentService.deletePayment(id))
                .build();
    }
}
