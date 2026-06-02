package com.project.hotelmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequest {
    @NotBlank(message = "CODE_INVALID")
    private String code;

    @NotBlank(message = "NUMBER_ROOM_INVALID")
    private String numberRoom;

    @NotBlank(message = "ROME_NAME_INVALID")
    private String name;

    private String description;

    @NotBlank(message = "ADDRESS_INVALID")
    private String address;

    @NotNull(message = "TYPE_ID_NOT_NULL")
    private Integer typeId;

    private List<MultipartFile> images;
}
