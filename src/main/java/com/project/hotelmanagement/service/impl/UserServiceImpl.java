package com.project.hotelmanagement.service.impl;

import com.project.hotelmanagement.dto.response.UserResponse;
import com.project.hotelmanagement.models.User;
import com.project.hotelmanagement.repository.UserRepository;
import com.project.hotelmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .phoneNumber(user.getPhoneNumber())
                        .password(user.getPassword())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .build())
                .collect(Collectors.toList());
    }
}
