package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.request.DiscountRequest;
import com.project.hotelmanagement.dto.response.ApiResponse;
import com.project.hotelmanagement.dto.response.DiscountResponse;
import com.project.hotelmanagement.service.impl.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/discount")
@RestController
@RequiredArgsConstructor
public class DiscountController {
    private final DiscountService discountService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    ApiResponse<List<DiscountResponse>> getDiscounts (){
        return ApiResponse.<List<DiscountResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("get all discounts")
                .result(discountService.getDiscounts())
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    ApiResponse<DiscountResponse> createDiscount (@Valid @RequestBody DiscountRequest request){
        return ApiResponse.<DiscountResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("create discount")
                .result(discountService.createDiscount(request))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    ApiResponse<DiscountResponse> updateDiscount (@PathVariable int id, @Valid @RequestBody DiscountRequest request){
        return ApiResponse.<DiscountResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("update discount")
                .result(discountService.updateDiscount(id,request))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    ApiResponse<?> deleteDiscount (@PathVariable int id){
       return ApiResponse.builder()
               .code(HttpStatus.NO_CONTENT.value())
               .message("delete discount")
               .result(discountService.deleteDiscount(id))
               .build();
    }
}
