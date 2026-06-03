package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.dto.request.PasswordRequest;
import com.project.hotelmanagement.dto.request.UserRequest;
import com.project.hotelmanagement.dto.response.ApiResponse;
import com.project.hotelmanagement.dto.response.UserResponse;
import com.project.hotelmanagement.enums.UserStatus;
import com.project.hotelmanagement.models.User;
import com.project.hotelmanagement.service.impl.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    ApiResponse<List<UserResponse>> getAllUsers () {
        return ApiResponse.<List<UserResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get all users")
                .result(userService.getAllUsers())
                .build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<UserResponse> createUser (@Valid @ModelAttribute UserRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("create user")
                .result(userService.createUser(request))
                .build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    ApiResponse<?> changeStatus (@PathVariable int id, String status){
        try {
            UserStatus userStatus = UserStatus.from(status);
            System.out.println("user status: " + userStatus);
            userService.changeStatus(id,userStatus);
            return new ApiResponse<>(HttpStatus.NO_CONTENT.value(),"change status successfully");
        } catch (RuntimeException e) {
            System.out.println("error: " + e.getMessage());
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(),"change status fail");

        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    ApiResponse<?> deleteUser (@PathVariable int id){
        try {
            userService.deleteUser(id);
            return new ApiResponse<>(HttpStatus.NO_CONTENT.value(),"delete user successfully");
        } catch (RuntimeException e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(),"delete user fail");
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    ApiResponse<UserResponse> updateUser (@PathVariable int id, @ModelAttribute UserRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("update user successfully")
                .result(userService.updateUser(id, request))
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    ApiResponse<UserResponse> getMyInfo (){
        return ApiResponse.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("my info")
                .result(userService.getMyInfo())
                .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    ApiResponse<?> changePassword (@RequestBody PasswordRequest request){
            return ApiResponse.builder()
                    .code(HttpStatus.ACCEPTED.value())
                    .message("change password success")
                    .result(userService.changePassword(request))
                    .build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/disconnect")
    ApiResponse<?> disconnect(@RequestBody User user) {
        userService.disconnect(user);
        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("User disconnected successfully")
                .build();
    }

    @MessageMapping("/user.connect")
    @SendTo("/user/topic")
    public User connectUser(@Payload User user) {
        try {
            userService.connectUser(user);
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect user: " + e.getMessage());
        }
    }

    @MessageMapping("/user.disconnect")
    @SendTo("/user/topic")
    public User disconnectUser(@Payload User user) {
        try {
            userService.disconnect(user);
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Failed to disconnect user: " + e.getMessage());
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/connected")
    ApiResponse<List<User>> findConnectedUsers(){
        return ApiResponse.<List<User>>builder()
                .code(HttpStatus.OK.value())
                .message("Get connected users successfully")
                .result(userService.findConnectedUsers())
                .build();
    }
}
