package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.request.LoginRequest;
import com.project.hotelmanagement.dto.response.ApiResponse;
import com.project.hotelmanagement.dto.response.AuthenticationResponse;
import com.project.hotelmanagement.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.<AuthenticationResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Login success")
                .result(authenticationService.authenticate(request))
                .build();
    }
}
