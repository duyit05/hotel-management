package com.project.hotelmanagement.utils;

import com.project.hotelmanagement.enums.GenderType;
import com.project.hotelmanagement.exception.AppException;
import com.project.hotelmanagement.exception.ErrorCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GenderTypeConverter implements AttributeConverter<GenderType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(GenderType gender) {
        if (gender == null) return null;
        return gender.getCode();
    }

    @Override
    public GenderType convertToEntityAttribute(Integer code) {
        if (code == null) return null;
        for (GenderType g : GenderType.values()) {
            if (g.getCode() == code) return g;
        }
        throw new AppException(ErrorCode.VALUE_GENDER_TYPE_INVALID);
    }
}
