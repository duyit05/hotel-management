package com.project.hotelmanagement.models;

import com.project.hotelmanagement.enums.BookingStatus;
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
@Table(name = "tbl_booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends AbstractEntity<Integer> {
    @Column(unique = true, nullable = false)
    private String bookingCode;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime actualCheckInTime;
    private LocalDateTime actualCheckOutTime;
    private Integer numberOfGuests;
    private Double totalAmount;
    private Double discountAmount;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private String specialRequest;
    private String note;


    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "handled_by")
    private User handledBy;

    @ManyToOne()
    @JoinColumn(name = "room_id")
    private Room room;



    @OneToMany(mappedBy = "booking", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<HealthyCare> healthyCares = new ArrayList<>();

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Invoice invoice;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "discount_id", referencedColumnName = "id")
    private Discount discount;
}
