package com.project.hotelmanagement.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.hotelmanagement.enums.RoomStatus;
import com.project.hotelmanagement.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room extends AbstractEntity<Integer>{
    private String code;
    private String numberRoom;
    private String name;
    private String description;
    private String address;
    @Enumerated(EnumType.STRING)
    private RoomStatus status;
    @ManyToOne
    @JoinColumn(name = "type_id")
    @JsonIgnore
    private Type type;

    @OneToMany(mappedBy = "room", cascade = {CascadeType.ALL})
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

}
