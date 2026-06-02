package com.project.hotelmanagement.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomResponse {
    private int id;
    private String code;
    private String numberRoom;
    private String name;
    private String description;
    private String address;
    private List<ImageResponse> images;
    private TypeResponse type;
}
