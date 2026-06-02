package com.project.hotelmanagement.mapper;

import com.project.hotelmanagement.dto.request.UserRequest;
import com.project.hotelmanagement.dto.response.UserResponse;
import com.project.hotelmanagement.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "avatar", ignore = true)
    User toUser (UserRequest request);
    UserResponse toUserResponse (User user);
    @Mapping(target = "avatar", ignore = true)
    void updateUser(@MappingTarget User user, UserRequest request);
}
