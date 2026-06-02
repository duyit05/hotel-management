package com.project.hotelmanagement.controller;

import com.project.hotelmanagement.service.impl.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/email")
@RestController
@Slf4j
@RequiredArgsConstructor
public class EmailController {
    private final EmailService service;

    @PostMapping("/send")
    public ResponseEntity<?> sendMail(
            @RequestParam String receive,
            @RequestParam String subject,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile[] files) throws MessagingException {
        try {
            return new ResponseEntity<>(service.sendEmail(receive, subject, content, files), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Send mail error: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
