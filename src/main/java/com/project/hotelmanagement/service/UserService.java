package com.project.hotelmanagement.service;

import com.project.hotelmanagement.dto.request.PasswordRequest;
import com.project.hotelmanagement.dto.request.UserRequest;
import com.project.hotelmanagement.dto.response.UserResponse;
import com.project.hotelmanagement.enums.UserStatus;
import com.project.hotelmanagement.models.User;
import com.project.hotelmanagement.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {
    List<UserResponse> getAllUsers ();
    UserResponse createUser (UserRequest request);
    UserResponse updateUser (int id, UserRequest request);
    void deleteUser (int id);

    void changeStatus (int id, UserStatus status);

    UserResponse getMyInfo ();
    boolean changePassword (PasswordRequest request);

}
