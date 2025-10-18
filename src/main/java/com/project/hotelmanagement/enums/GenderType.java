package com.project.hotelmanagement.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum GenderType {
    MALE(0,"male"),
    FEMALE(1,"female"),
    OTHER(2,"other");

    private final int code;
    private final String label;

    GenderType(int code, String label) {
        this.code = code;
        this.label = label;
    }

    @JsonValue
    public Object getJsonValue() {
        // Trả về code gửi 0,1,2
        // Trả về label gửi "male","female","other"
        return label;
    }

    @JsonCreator
    public static GenderType from(Object value) {
        if (value instanceof Number) {
            int code = ((Number) value).intValue();
            for (GenderType g : values()) {
                if (g.code == code) return g;
            }
        } else if (value instanceof String) {
            String text = ((String) value).trim().toLowerCase();
            for (GenderType g : values()) {
                if (g.label.equalsIgnoreCase(text)) return g;
            }
        }
        throw new AppException(ErrorCode.VALUE_GENDER_TYPE_INVALID);
    }
}

