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
    // ERROR SYSTEM
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    // ERROR USER
    USER_NOT_EXIST(1005, "User not existed", HttpStatus.NOT_FOUND),
    USER_INACTIVE(1006, "User is inactive", HttpStatus.BAD_REQUEST),
    USER_BLOCK(1007, "User is block", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1008, "User existed", HttpStatus.BAD_REQUEST),

    USERNAME_NOT_BLANK(1010,"Username is not blank", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1011,"Password at least 6 characters", HttpStatus.BAD_REQUEST),
    FIRST_NAME_INVALID(1012,"First name is not blank", HttpStatus.BAD_REQUEST),
    LAST_NAME_INVALID(1013,"Last name is not blank", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_INVALID(1014,"Phone number invalid", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1015,"Email invalid", HttpStatus.BAD_REQUEST),
    DATE_OR_BIRTH_NOT_NULL(1016,"Date or birth must not be null", HttpStatus.BAD_REQUEST),


    // AUTHENTICATION
    PASSWORD_INCORRECT(2001, "Password incorrect", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(2002, "You do not have a permission", HttpStatus.FORBIDDEN),

    // ENUM GENDER TYPE
    VALUE_GENDER_TYPE_INVALID (3001,"Value gender type must contains in (0, 1, 2)", HttpStatus.BAD_REQUEST),
    VALUE_USER_STATUS_INVALID (3002,"Value user status must contains in (0, 1, 2)", HttpStatus.BAD_REQUEST),

    // ENUM ROLE TYPE
    ROLE_NOT_FOUND (4001,"Role not found", HttpStatus.BAD_REQUEST);


    int code;
    String message;
    HttpStatusCode statusCode;
}
