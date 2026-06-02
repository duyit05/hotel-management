package com.project.hotelmanagement.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceItem extends AbstractEntity<Integer>{
    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false, length = 50)
    private Integer unit;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isAvailable;

    @PrePersist
    protected void onCreate() {
        if (isAvailable == null) {
            isAvailable = true;
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ServiceCategory category;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    private List<HealthyCare> healthyCare = new ArrayList<>();
}