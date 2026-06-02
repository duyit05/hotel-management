package com.project.hotelmanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ApiResponse <T> {
    int code;
    String message;
    T result;

     public ApiResponse (int code, String message){
        this.code = code;
        this.message = message;
    }
}
