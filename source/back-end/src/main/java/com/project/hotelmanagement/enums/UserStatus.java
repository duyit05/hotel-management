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
        return code;
    }

    @JsonCreator
    public static UserStatus from(Object value) {
        if (value == null) return null;
        String text = String.valueOf(value).trim();
        try {
            int code = Integer.parseInt(text);
            for (UserStatus u : values()) {
                if (u.code == code) return u;
            }
        } catch (NumberFormatException ignored) {}
        for (UserStatus u : values()) {
            if (u.label.equalsIgnoreCase(text)) return u;
        }
        throw new AppException(VALUE_USER_STATUS_INVALID);
    }

    public static UserStatus fromCode(int code) {
        for (UserStatus u : values()) {
            if (u.code == code) return u;
        }
        throw new AppException(VALUE_USER_STATUS_INVALID);
    }
}
