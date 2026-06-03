package com.project.hotelmanagement.utils;

import com.project.hotelmanagement.enums.GenderType;
import com.project.hotelmanagement.enums.UserStatus;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.exception.ErrorCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserStatusConverter implements AttributeConverter<UserStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(UserStatus status) {
        if (status == null) return null;
        return status.getCode();
    }

    @Override
    public UserStatus convertToEntityAttribute(Integer code) {
        if (code == null) return null;
        for (UserStatus g : UserStatus.values()) {
            if (g.getCode() == code) return g;
        }
        throw new AppException(ErrorCode.VALUE_GENDER_TYPE_INVALID);
    }
}
