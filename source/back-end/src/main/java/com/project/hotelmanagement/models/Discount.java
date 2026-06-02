package com.project.hotelmanagement.models;

import com.project.hotelmanagement.enums.DiscountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "tbl_discount")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Discount extends AbstractEntity<Integer>{
    private String code;
    private String name;
    private String description;
    private Double discountValue;
    private Double maxDiscountAmount;
    private Double minOrderAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer usingLimit;
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private DiscountType discount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "discount", cascade = CascadeType.ALL)
    private Booking booking;
}
