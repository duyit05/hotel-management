package com.project.hotelmanagement.models;

import com.project.hotelmanagement.enums.GenderType;
import com.project.hotelmanagement.enums.StatusChat;
import com.project.hotelmanagement.enums.UserRank;
import com.project.hotelmanagement.enums.UserStatus;
import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "tbl_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractEntity <Integer> implements UserDetails {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private UserRank rank;

    private String national;
    private String idCard;


    private GenderType gender;

    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private StatusChat chatStatus;

    private Date dateOrBirth;
    private String activeCode;

    @Column(columnDefinition = "TEXT")
    private String avatar;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<UserHasRole> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Discount> discounts = new ArrayList<>();

    @OneToMany(mappedBy = "processedBy", fetch = FetchType.LAZY, cascade =  CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "handledBy")
    private List<Booking> handledBookings = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       if (roles ==  null) return null;
       return roles.stream()
               .map(UserHasRole::getRole)
               .filter(Objects::nonNull)
               .map(Role::getRoleName)
               .map(SimpleGrantedAuthority::new)
               .collect(Collectors.toList());

    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
