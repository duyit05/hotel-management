package com.project.hotelmanagement.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_img")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image extends AbstractEntity<Integer>{
    private String url;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}
