package com.project.hotelmanagement.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_EXIST(1005, "User not existed", HttpStatus.NOT_FOUND),
    USER_INACTIVE(1006, "User is inactive", HttpStatus.UNAUTHORIZED),
    USER_BLOCK(1007, "User is block", HttpStatus.UNAUTHORIZED),
    PASSWORD_INCORRECT(2001, "Password incorrect", HttpStatus.UNAUTHORIZED);


    int code;
    String message;
    HttpStatusCode statusCode;
}
