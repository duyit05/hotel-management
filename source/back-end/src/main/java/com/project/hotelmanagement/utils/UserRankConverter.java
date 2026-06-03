package com.project.hotelmanagement.utils;

import com.project.hotelmanagement.enums.GenderType;
import com.project.hotelmanagement.enums.UserRank;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.exception.ErrorCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRankConverter implements AttributeConverter<UserRank, Integer> {
    @Override
    public Integer convertToDatabaseColumn(UserRank rank) {
        if (rank == null) return null;
        return rank.getCode();
    }

    @Override
    public UserRank convertToEntityAttribute(Integer code) {
        if (code == null) return null;
        for (UserRank g : UserRank.values()) {
            if (g.getCode() == code) return g;
        }
        throw new AppException(ErrorCode.VALUE_GENDER_TYPE_INVALID);
    }
}
