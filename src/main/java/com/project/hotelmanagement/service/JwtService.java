package com.project.hotelmanagement.service;

import com.project.hotelmanagement.enums.TokenType;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface JwtService {
    String generateToken (UserDetails user, List<String> authorities);
    String refreshToken (UserDetails userDetails,List<String> authorities);
    public String extractUsername(String token, TokenType type);
}
