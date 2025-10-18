package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.response.ApiResponse;
import com.project.hotelmanagement.dto.response.UserResponse;
import com.project.hotelmanagement.models.User;
import com.project.hotelmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    ApiResponse<List<UserResponse>> getAllUsers () {
        return ApiResponse.<List<UserResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get all users")
                .result(userService.getAllUsers())
                .build();
    }


}
