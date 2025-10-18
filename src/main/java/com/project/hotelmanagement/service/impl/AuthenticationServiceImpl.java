package com.project.hotelmanagement.service.impl;

import com.project.hotelmanagement.dto.request.LoginRequest;
import com.project.hotelmanagement.dto.response.AuthenticationResponse;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.models.User;
import com.project.hotelmanagement.repository.UserRepository;
import com.project.hotelmanagement.service.AuthenticationService;
import com.project.hotelmanagement.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.project.hotelmanagement.enums.UserStatus.*;
import static com.project.hotelmanagement.exception.ErrorCode.*;

;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthenticationResponse authenticate(LoginRequest request) {
        List<String> authorities = new ArrayList<>();
        try {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new AppException(USER_NOT_EXIST));
            if (user.getStatus().equals(INACTIVE)) throw new AppException(USER_INACTIVE);

            if (user.getStatus().equals(BLOCK)) throw new AppException(USER_BLOCK);

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
                throw new AppException(PASSWORD_INCORRECT);

            if (user.getStatus().equals(ACTIVE)) {
                Authentication authentication = authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
                log.info("isAuthenticated : {}", authentication.isAuthenticated());
                log.info("Authorities : {}", authentication.getAuthorities().toString());

                authentication.getAuthorities().forEach(auth -> authorities.add(auth.getAuthority()));
                String accessToken = jwtService.generateToken(user, authorities);
                String refreshToken = jwtService.refreshToken(user, authorities);

                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .roles(authorities)
                        .build();
            }
        } catch (BadCredentialsException | DisabledException e) {
            log.error("errorMessage: {}", e.getMessage());
            throw new BadCredentialsException(e.getMessage());
        }
        return null;
    }
}

