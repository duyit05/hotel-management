package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.request.ServiceItemRequest;
import com.project.hotelmanagement.dto.response.ApiResponse;
import com.project.hotelmanagement.dto.response.ServiceItemResponse;
import com.project.hotelmanagement.service.impl.ServiceItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/service/item")
@RestController
@RequiredArgsConstructor
public class ServiceItemController {
    private final ServiceItemService service;

    @GetMapping
    ApiResponse<List<ServiceItemResponse>> getServiceItems (){
        return ApiResponse.<List<ServiceItemResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("get all service items")
                .result(service.getServices())
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    ApiResponse<ServiceItemResponse> createServiceItem (@Valid @RequestBody ServiceItemRequest request){
        return ApiResponse.<ServiceItemResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("create service item")
                .result(service.createService(request))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    ApiResponse<ServiceItemResponse> updateServiceItem (@PathVariable int id ,@RequestBody ServiceItemRequest request){
        return ApiResponse.<ServiceItemResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("update service item")
                .result(service.updateService(id, request))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    ApiResponse<?> deleteServiceItem (@PathVariable int id){
        return ApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("delete service item")
                .result(service.deleteService(id))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<ServiceItemResponse> getDetail (@PathVariable int id){
        return ApiResponse.<ServiceItemResponse>builder()
                .code(HttpStatus.OK.value())
                .message("get detail service item")
                .result(service.getDetailService(id))
                .build();
    }
}
