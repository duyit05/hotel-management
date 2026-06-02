package com.project.hotelmanagement.models;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "tbl_healthy_care")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthyCare extends AbstractEntity<Integer>{
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private ServiceItem service;

    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
    private LocalDate registerDate;
    private String note;


}
