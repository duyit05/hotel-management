package com.project.hotelmanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.hotelmanagement.enums.GenderType;
import com.project.hotelmanagement.validator.GenderSubset;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

import static com.project.hotelmanagement.enums.GenderType.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @NotBlank(message = "USERNAME_NOT_BLANK")
    private String username;

    @Size(min = 6, message = "PASSWORD_INVALID")
    private String password;

    @NotBlank(message = "FIRST_NAME_INVALID")
    private String firstName;

    @NotBlank(message = "LAST_NAME_INVALID")
    private String lastName;

    @Pattern(regexp = "^\\d{10}$" , message = "PHONE_NUMBER_INVALID")
    private String phoneNumber;

    @Email(message = "EMAIL_INVALID")
    private String email;

    @GenderSubset(anyOf = {MALE,FEMALE,OTHER})
    private GenderType gender;

    @NotNull(message = "DATE_OR_BIRTH_NOT_NULL")
    @DateTimeFormat(pattern = "MM/dd/yyyy") // cho form-data
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy") // cho JSON
    private Date dateOrBirth;

    private MultipartFile avatar;

}
