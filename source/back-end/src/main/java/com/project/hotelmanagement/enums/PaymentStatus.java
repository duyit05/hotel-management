package com.project.hotelmanagement.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    PENDING(0, "pending"),
    COMPLETED(1, "completed"),
    FAILED(2, "failed"),
    REFUNDED(3, "refunded");

    private final int code;
    private final String label;

    @JsonValue
    public Object getJsonValue () {
        return label;
    }

    @JsonCreator
    public static PaymentStatus from (Object value){
        if(value instanceof Number){
            int code = ((Number) value).intValue();
            for (PaymentStatus p : values()){
                if(p.code == code) return p;
            }
        }else if (value instanceof String){
            String label = ((String) value).trim().toLowerCase();
            for (PaymentStatus p: values()){
                if(p.label.equalsIgnoreCase(label)) return p;
            }
        }
        throw new AppException(ErrorCode.PAYMENT_STATUS_INVALID);
    }
}
