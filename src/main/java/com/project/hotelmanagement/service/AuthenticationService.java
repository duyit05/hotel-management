package com.project.hotelmanagement.service;

import com.project.hotelmanagement.dto.request.LoginRequest;
import com.project.hotelmanagement.dto.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse authenticate (LoginRequest request);
}
