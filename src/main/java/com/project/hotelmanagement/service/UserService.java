package com.project.hotelmanagement.service;

import com.project.hotelmanagement.dto.response.UserResponse;
import com.project.hotelmanagement.models.User;
import com.project.hotelmanagement.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {
    List<UserResponse> getAllUsers ();

}
