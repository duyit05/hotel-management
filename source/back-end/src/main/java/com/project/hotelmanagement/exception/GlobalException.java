package com.project.hotelmanagement.exception;

import com.project.hotelmanagement.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TypeMismatchException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

import static com.project.hotelmanagement.exception.ErrorCode.*;

@ControllerAdvice
@Slf4j
public class GlobalException {
    // -----------------------------
    // 1️⃣ Bắt tất cả RuntimeException chưa được xử lý
    // -----------------------------
//    @ExceptionHandler(value = RuntimeException.class)
//    ResponseEntity<ApiResponse<?>> handlingException() {
//        ErrorCode errorCode = UNCATEGORIZED_EXCEPTION;
//        return ResponseEntity.status(errorCode.getStatusCode())
//                .body(ApiResponse.builder()
//                        .code(errorCode.getCode())
//                        .message(errorCode.getMessage())
//                        .build());
//    }
    // -----------------------------
    // 2️⃣ Bắt các AppException tùy chỉnh hệ thống
    // -----------------------------
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.badRequest()
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
    // -----------------------------
    // 3️⃣ Bắt validation errors của @RequestBody + @Valid
    // -----------------------------
    // EXCEPTION FOR INPUT
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        ErrorCode errorCode = ErrorCode.valueOf(enumKey);

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
    // -----------------------------
    // 4️⃣ Bắt lỗi khi binding @ModelAttribute hoặc form params
    //    - BindException
    //    - ConversionFailedException
    //    - TypeMismatchException
    // -----------------------------
    // EXCEPTION FOR BINDING
    @ExceptionHandler({BindException.class, ConversionFailedException.class, TypeMismatchException.class})
    public ResponseEntity<ApiResponse<?>> handleTypeMismatch(Exception ex) {
        log.info("exception: {}", ex.getMessage());
        ErrorCode errorCode = ErrorCode.VALUE_GENDER_TYPE_INVALID;

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    // -----------------------------
    // 5️⃣ Bắt lỗi deserialize enum từ JSON @RequestBody
    //    - HttpMessageNotReadableException
    //    - Chỉ dùng cho enum (GenderType, PaymentMethod, PaymentStatus)
    // -----------------------------
    // EXCEPTION FOR INPUT WITH ENUM
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    ResponseEntity<ApiResponse<?>> handleEnumValidationEnum(HttpMessageNotReadableException ex) {
//        ErrorCode errorCode = VALUE_GENDER_TYPE_INVALID;
//        return ResponseEntity.status(errorCode.getStatusCode()).body(
//                ApiResponse.builder()
//                        .code(errorCode.getCode())
//                        .message(errorCode.getMessage())
//                        .build()
        String message = ex.getMessage();
        ErrorCode errorCode = INVALID_REQUEST;

        if(message.contains("GenderType")){
            errorCode = VALUE_GENDER_TYPE_INVALID;
        }else if (message.contains("PaymentMethod")) {
            errorCode = ErrorCode.PAYMENT_METHOD_INVALID;
        }
        else if (message.contains("PaymentStatus")) {
            errorCode = ErrorCode.PAYMENT_STATUS_INVALID;
        }
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
    // -----------------------------
    // 6️⃣ Bắt lỗi AccessDenied từ Spring Security
    //    - @PreAuthorize hoặc AccessDeniedException
    // -----------------------------
    @ExceptionHandler(value = {
            AuthorizationDeniedException.class,  // Từ @PreAuthorize
            AccessDeniedException.class          // Từ Spring Security (nếu leak qua)
    })
    ResponseEntity<ApiResponse<?>> handlingAccessDeniedException(Exception exception) {
        log.error("Access Denied: {}", exception.getMessage());
        ErrorCode errorCode = FORBIDDEN;
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}
