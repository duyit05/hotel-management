package com.project.hotelmanagement.dto.response;

import com.project.hotelmanagement.enums.GenderType;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private GenderType gender;
    private Date dateOrBirth;

}
