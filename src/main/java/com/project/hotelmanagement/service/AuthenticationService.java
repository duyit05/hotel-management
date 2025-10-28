package com.project.hotelmanagement.service;

import com.project.hotelmanagement.dto.request.LoginRequest;
import com.project.hotelmanagement.dto.response.AuthenticationResponse;
import com.project.hotelmanagement.models.User;

public interface AuthenticationService {
    AuthenticationResponse authenticate (LoginRequest request);

    String getUserFromContext();

    User findByUsername (String username);
}
