package com.project.hotelmanagement.models;

import com.project.hotelmanagement.enums.PaymentMethod;
import com.project.hotelmanagement.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "tbl_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends AbstractEntity<Integer>{
    @Column(unique = true, nullable = false)
    private String paymentCode;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private Date paymentDate;
    private String note;

    @ManyToOne
    @JoinColumn(name = "processed_by")
    private User processedBy;


    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
}
