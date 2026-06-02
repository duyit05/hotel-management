package com.project.hotelmanagement.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender sender;

    @Value("${spring.mail.from}")
    private String emailFrom;

    
    public String sendEmail(String receive, String subject, String content, MultipartFile[] files) throws MessagingException {
       log.info("Sending .....");
       try {
           MimeMessage message = sender.createMimeMessage();
           MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
           helper.setFrom(emailFrom, "Duy Dev");
           if(receive.contains(",")){
               helper.setTo(InternetAddress.parse(receive));
           }else {
               helper.setTo(receive);
           }
           if(files != null){
               for (MultipartFile file : files){
                   helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
               }
           }
           helper.setSubject(subject);
           helper.setText(content, true);
           sender.send(message);
       }catch (Exception e){
            log.error("Exception:{}", e.getMessage());
       }
        log.info("Send success....., receive:{}", receive);
        return "sent";
    }
}