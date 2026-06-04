package com.project.hotelmanagement.service.impl;

import com.project.hotelmanagement.dto.request.PasswordRequest;
import com.project.hotelmanagement.dto.request.UserRequest;
import com.project.hotelmanagement.dto.response.PageResponse;
import com.project.hotelmanagement.dto.response.UserResponse;
import com.project.hotelmanagement.enums.GenderType;
import com.project.hotelmanagement.enums.UserRank;
import com.project.hotelmanagement.enums.UserStatus;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.integration.MinioChannel;
import com.project.hotelmanagement.mapper.UserMapper;
import com.project.hotelmanagement.models.Role;
import com.project.hotelmanagement.models.User;
import com.project.hotelmanagement.models.UserHasRole;
import com.project.hotelmanagement.repository.RoleRepository;
import com.project.hotelmanagement.repository.UserRepository;
import com.project.hotelmanagement.repository.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.project.hotelmanagement.enums.RoleType.USER;
import static com.project.hotelmanagement.enums.StatusChat.OFFLINE;
import static com.project.hotelmanagement.enums.StatusChat.ONELINE;
import static com.project.hotelmanagement.enums.UserRank.STANDARD;
import static com.project.hotelmanagement.enums.UserStatus.INACTIVE;
import static com.project.hotelmanagement.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final MinioChannel minioChannel;
    private final AuthenticationService auth;
    private final UserMapper userMapper;



    public PageResponse<?> getAllUsers(int pageNo, int pageSize, String keyword,
                                       Integer statusCode, Integer rankCode, Integer genderCode,
                                       String ...sorts) {
        int page = pageNo > 0 ? pageNo - 1 : 0;
        // Xử lý sort
        List<Sort.Order> orders = new ArrayList<>();
        if(sorts != null){
            for (String sortBy : sorts) {
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(sortBy);
                if (matcher.find()) {
                    String fieldName = matcher.group(1);
                    String direction = matcher.group(3);
                    orders.add(new Sort.Order(
                            direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                            fieldName
                    ));
                }
            }
        }
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(orders));

        // Convert Integer -> Enum
        UserStatus status = statusCode != null ? UserStatus.fromCode(statusCode) : null;
        UserRank rank = rankCode != null ? UserRank.fromCode(rankCode) : null;
        GenderType gender = genderCode != null ? GenderType.fromCode(genderCode) : null;

        // Dùng Specification thay vì JPQL query
        Specification<User> spec = Specification
                .where(UserSpecification.hasKeyword(keyword))
                .and(UserSpecification.hasStatus(status))
                .and(UserSpecification.hasRank(rank))
                .and(UserSpecification.hasGender(gender));

        Page<User> users = userRepository.findAll(spec, pageable);

        List<UserResponse> responses = users.getContent()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
        int fakeTotalPage = pageNo + (users.hasNext() ? 5 : 1);
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(users.getTotalPages())
                .items(responses)
                .build();
    }

    
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(USER_EXISTED);
        }
        User user = userMapper.toUser(request);
        user.setStatus(INACTIVE);
        user.setAvatar(uploadAvatar(request.getAvatar()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRank(STANDARD);

        Role role = roleRepository.findByRoleName(String.valueOf(USER))
                .orElseThrow(() -> new AppException(ROLE_NOT_FOUND));

        UserHasRole userRoles = new UserHasRole();
        userRoles.setUser(user);
        userRoles.setRole(role);

        List<UserHasRole> roles = new ArrayList<>();
        roles.add(userRoles);
        user.setRoles(roles);

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    private User findById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new AppException(USER_NOT_EXIST));
    }

    
    public UserResponse updateUser(int id, UserRequest request) {
        User user = findById(id);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            user.setAvatar(minioChannel.update(request.getAvatar()));
        } else {
            user.setAvatar(null);
        }
        user.setAvatar(uploadAvatar(request.getAvatar()));
        userMapper.updateUser(user, request);
        return userMapper.toUserResponse(user);
    }

    
    public void deleteUser(int id) {
        User user = findById(id);
        userRepository.delete(user);
        log.info("delete user successfully");
    }

    
    public void changeStatus(int id, UserStatus status) {
        User user = findById(id);
        user.setStatus(status);
        userRepository.save(user);
        log.info("change status successfully");
    }

    
    public UserResponse getMyInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(USER_NOT_EXIST));
        return userMapper.toUserResponse(user);
    }

    
    public boolean changePassword(PasswordRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("username: {}", username);
            User user = auth.findByUsername(username);

            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new AppException(OLD_PASSWORD_INVALID);
            }
            if (!request.getNewPassword().equals(request.getNewPasswordRepeat())) {
                throw new AppException(NEW_PASSWORD_NOT_MATCH);
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            return true;
        } catch (BadCredentialsException e) {
            log.error("Error: {}", e.getMessage());
        }
        return false;
    }

    
    public void connectUser(User user) {
        User existingUser = findById(user.getId());
        existingUser.setChatStatus(ONELINE);
        userRepository.save(existingUser);
        log.info("User {} connected", existingUser.getUsername());
    }

    
    public void disconnect(User user)  {
        User userDisconnect = findById(user.getId());
        userDisconnect.setChatStatus(OFFLINE);
        userRepository.save(userDisconnect);
        log.info("User {} disconnected", userDisconnect.getUsername());
    }

    
    public List<User> findConnectedUsers() {
        return userRepository.findAlByChatStatus(ONELINE);
    }

    private String uploadAvatar (MultipartFile avatar){
        String imgAvatar = null;
        if(avatar != null && !avatar.isEmpty()){
            imgAvatar = minioChannel.update(avatar);
        }
        return imgAvatar;
    }
}
