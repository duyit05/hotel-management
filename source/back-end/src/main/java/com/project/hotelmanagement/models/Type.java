package com.project.hotelmanagement.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Type extends AbstractEntity<Integer>{

    private String name;
    private double basicPrice;
    private String type;
    private int maxOccupancy;
    private String amenities;

    @OneToMany(mappedBy = "type")
    private List<Room> rooms = new ArrayList<>();
}
