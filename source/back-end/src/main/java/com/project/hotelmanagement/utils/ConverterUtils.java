package com.project.hotelmanagement.utils;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Component
public class ConverterUtils {
    public static byte[] readBytesFromInputStream(InputStream inputStream, int length) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[length];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }
}
