package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.request.TypeRequest;
import com.project.hotelmanagement.dto.response.ApiResponse;
import com.project.hotelmanagement.dto.response.TypeResponse;
import com.project.hotelmanagement.service.TypeService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/types")
@RestController
@RequiredArgsConstructor
public class TypeController {
    private final TypeService typeService;

    @GetMapping
    ApiResponse<List<TypeResponse>> getTypes (){
        return ApiResponse.<List<TypeResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get all types")
                .result(typeService.getTypes())
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    ApiResponse<TypeResponse> createType (@Valid @RequestBody TypeRequest request){
        System.out.println("Đã chạy vào đây");
        return ApiResponse.<TypeResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Create type")
                .result(typeService.createType(request))
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    ApiResponse<?> deleteType (@PathVariable int id){
        boolean isDeleted = typeService.deleteType(id);
        return ApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Delete type")
                .result(isDeleted)
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    ApiResponse<TypeResponse> updateType (@PathVariable int id, @RequestBody @Valid TypeRequest request){
        return ApiResponse.<TypeResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Delete type")
                .result(typeService.updateType(id,request))
                .build();
    }
}
