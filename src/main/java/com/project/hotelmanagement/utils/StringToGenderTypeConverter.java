package com.project.hotelmanagement.utils;

import com.project.hotelmanagement.enums.GenderType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToGenderTypeConverter implements Converter<String, GenderType> {

    @Override
    public GenderType convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        try {
            // Nếu là số (0,1,2)
            int code = Integer.parseInt(source);
            for (GenderType g : GenderType.values()) {
                if (g.getCode() == code) {
                    return g;
                }
            }
        } catch (NumberFormatException e) {
            // Nếu là chữ (male, MALE, Female, etc.)
            for (GenderType g : GenderType.values()) {
                if (g.name().equalsIgnoreCase(source)
                        || g.getLabel().equalsIgnoreCase(source)) {
                    return g;
                }
            }
        }
        throw new IllegalArgumentException("Invalid gender: " + source);
    }

}
