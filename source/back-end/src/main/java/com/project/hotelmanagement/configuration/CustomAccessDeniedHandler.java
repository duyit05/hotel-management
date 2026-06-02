package com.project.hotelmanagement.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotelmanagement.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Map<String, Object> body = new HashMap<>();
        body.put("code", ErrorCode.FORBIDDEN.getCode());
        body.put("message", ErrorCode.FORBIDDEN.getMessage());

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}
