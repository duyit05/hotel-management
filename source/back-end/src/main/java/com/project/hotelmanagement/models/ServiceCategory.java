package com.project.hotelmanagement.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_service_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCategory extends AbstractEntity<Integer>{
    @Column(nullable = false, unique = true)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean isActive;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ServiceItem> services = new ArrayList<>();
}
