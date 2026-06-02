package com.project.hotelmanagement.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CASH(0, "cash"),
    CREDIT_CARD(1, "credit_card"),
    MOMO(2, "momo"),
    VNPAY(3, "vnpay");

    private final int code;
    private final String label;

    @JsonValue
    public Object getJsonValue () {
        return label;
    }

    @JsonCreator
    public static PaymentMethod from (Object value){
        if(value instanceof Number){
            int code = ((Number) value).intValue();
            for (PaymentMethod p : values()){
                if(p.code == code) return p;
            }
        }else if (value instanceof String){
            String label = ((String) value).trim().toLowerCase();
            for (PaymentMethod p: values()){
                if(p.label.equalsIgnoreCase(label)) return p;
            }
        }
        throw new AppException(ErrorCode.PAYMENT_METHOD_INVALID);
    }
}
