package com.project.hotelmanagement.dto.response;

import lombok.*;

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
}
