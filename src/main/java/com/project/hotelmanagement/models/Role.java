package com.project.hotelmanagement.models;


import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends AbstractEntity<Integer>{

    private String roleName;

    @OneToMany(mappedBy = "role")
    private List<UserHasRole> roles = new ArrayList<>();
}
