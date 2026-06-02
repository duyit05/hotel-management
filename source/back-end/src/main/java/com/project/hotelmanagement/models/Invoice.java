package com.project.hotelmanagement.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "tbl_invoice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invoice extends AbstractEntity<Integer>{
    private String invoiceNumber;
    private Date issueDate;
    private Double roomCharge;
    private Double serviceCharge;
    private Double discountAmount;
    private Double totalAmount;
    private String note;
    private Boolean isPaid;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "booking_id", referencedColumnName = "id")
    private Booking booking;
}
