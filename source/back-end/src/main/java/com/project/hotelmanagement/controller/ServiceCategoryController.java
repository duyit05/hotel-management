package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.request.ServiceCategoryRequest;
import com.project.hotelmanagement.dto.response.ApiResponse;
import com.project.hotelmanagement.dto.response.ServiceCategoryResponse;
import com.project.hotelmanagement.service.impl.ServiceCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/service/category")
@RestController
@RequiredArgsConstructor
public class ServiceCategoryController {
    private final ServiceCategoryService service;

    @GetMapping
    ApiResponse<List<ServiceCategoryResponse>> getServiceCategories (){
        return ApiResponse.<List<ServiceCategoryResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("get service categories")
                .result(service.getServiceCategories())
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    ApiResponse<ServiceCategoryResponse> createServiceCategory (@Valid @RequestBody ServiceCategoryRequest request){
        return ApiResponse.<ServiceCategoryResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("create service category")
                .result(service.createServiceCategory(request))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    ApiResponse<ServiceCategoryResponse> updateServiceCategory (@PathVariable int id, @RequestBody ServiceCategoryRequest request){
        return ApiResponse.<ServiceCategoryResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("update service category")
                .result(service.updateServiceCategory(id,request))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    ApiResponse<?> createServiceCategory (@PathVariable int id){
        return ApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("create service category")
                .result(service.deleteServiceCategory(id))
                .build();
    }
 }
