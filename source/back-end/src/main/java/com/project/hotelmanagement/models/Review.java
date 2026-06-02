package com.project.hotelmanagement.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "tbl_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review extends AbstractEntity<Integer>{
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private Double rating;
    private String comment;
    private Date reviewDate;
    private Boolean isApproved;
}
