package com.project.hotelmanagement.service.impl;

import com.project.hotelmanagement.dto.request.UserRequest;
import com.project.hotelmanagement.dto.response.UserResponse;
import com.project.hotelmanagement.enums.UserStatus;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.models.Role;
import com.project.hotelmanagement.models.User;
import com.project.hotelmanagement.models.UserHasRole;
import com.project.hotelmanagement.repository.RoleRepository;
import com.project.hotelmanagement.repository.UserHasRoleRepository;
import com.project.hotelmanagement.repository.UserRepository;
import com.project.hotelmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.project.hotelmanagement.enums.RoleType.USER;
import static com.project.hotelmanagement.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .phoneNumber(user.getPhoneNumber())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .gender(user.getGender())
                        .dateOrBirth(user.getDateOrBirth())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        if(userRepository.existsByUsername(request.getUsername())){
            throw new AppException(USER_EXISTED);
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender());
        user.setDateOrBirth(request.getDateOrBirth());

        Role role = roleRepository.findByRoleName(String.valueOf(USER))
                .orElseThrow(() -> new AppException(ROLE_NOT_FOUND));

        UserHasRole userRoles = new UserHasRole();
        userRoles.setUser(user);
        userRoles.setRole(role);

        List<UserHasRole> roles = new ArrayList<>();
        roles.add(userRoles);

        user.setRoles(roles);

        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .email(user.getEmail())
                .dateOrBirth(user.getDateOrBirth())
                .build();
    }

    private User findById (int id){
        return userRepository.findById(id).orElseThrow(() -> new AppException(USER_NOT_EXIST));
    }

    @Override
    public UserResponse updateUser(int id, UserRequest request) {
        User user = findById(id);
        user.setLastName(request.getLastName());
        user.setFirstName(request.getFirstName());
        user.setDateOrBirth(request.getDateOrBirth());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setGender(request.getGender());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .email(user.getEmail())
                .dateOrBirth(user.getDateOrBirth())
                .build();
    }

    @Override
    public void deleteUser(int id) {
        User user = findById(id);
        userRepository.delete(user);
        log.info("delete user successfully");
    }

    @Override
    public void changeStatus(int id, UserStatus status) {
        User user = findById(id);
        user.setStatus(status);
        userRepository.save(user);
        log.info("change status successfully");
    }

    @Override
    public UserResponse getMyInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(USER_NOT_EXIST));

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .email(user.getEmail())
                .dateOrBirth(user.getDateOrBirth())
                .build();
    }
}
