package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.request.HealthyCareRequest;
import com.project.hotelmanagement.dto.response.ApiResponse;
import com.project.hotelmanagement.dto.response.HealthyCareResponse;
import com.project.hotelmanagement.service.impl.HealthyCareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/health/care")
@RestController
@RequiredArgsConstructor
public class HealthyCareController {
    private final HealthyCareService service;
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    ApiResponse<List<HealthyCareResponse>> getHealthyCares (){
        return ApiResponse.<List<HealthyCareResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("get healthy cares")
                .result(service.getHealthyCares())
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    ApiResponse<HealthyCareResponse> createHealthyCare(@Valid @RequestBody HealthyCareRequest request){
        return ApiResponse.<HealthyCareResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("create healthy care success")
                .result(service.createHealthyCare(request))
                .build();
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    ApiResponse<HealthyCareResponse>updateHealthyCare(Integer id, @RequestBody HealthyCareRequest request){
        return ApiResponse.<HealthyCareResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("update healthy care success")
                .result(service.updateHealThyCate(id,request))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    ApiResponse<Boolean>deleteHealthyCare(@PathVariable Integer id){
        boolean isDeleted = service.deleteHealThyCare(id);
        if (isDeleted) {
            return ApiResponse.<Boolean>builder()
                    .code(HttpStatus.OK.value())
                    .message("Delete healthy care successfully")
                    .result(true)
                    .build();
        } else {
            return ApiResponse.<Boolean>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Delete healthy care fail")
                    .result(false)
                    .build();
        }
    }
}
