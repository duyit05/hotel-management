package com.project.hotelmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String newPasswordRepeat;
}
