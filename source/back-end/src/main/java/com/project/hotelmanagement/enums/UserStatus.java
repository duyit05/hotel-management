package com.project.hotelmanagement.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.project.hotelmanagement.exception.AppException;
import lombok.Getter;

import static com.project.hotelmanagement.exception.ErrorCode.VALUE_USER_STATUS_INVALID;

@Getter
public enum UserStatus {


    ACTIVE(0,"active"),
    INACTIVE(1,"inactive"),
    BLOCK(2,"block");

    private final int code;
    private final String label;

    UserStatus (int code, String label){
        this.code = code;
        this.label = label;
    }

    @JsonValue
    public Object getValue () {
        return label;
    }

    @JsonCreator
    public static UserStatus from (Object value){
            if(value instanceof  Number){
                int code = ((Number) value).intValue();
                for (UserStatus u : values()){
                    if (u.code == code) return u;
                }
            } else if (value instanceof  String) {
                String text = ((String) value).trim().toLowerCase();
                for(UserStatus u : values()){
                    if (u.label.equalsIgnoreCase(text)) return u;
                }
            }
            throw  new AppException(VALUE_USER_STATUS_INVALID);
    }
}
