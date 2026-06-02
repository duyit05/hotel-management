package com.project.hotelmanagement.repository;

import com.project.hotelmanagement.models.Booking;
import com.project.hotelmanagement.models.HealthyCare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthyCareRepository extends JpaRepository<HealthyCare, Integer> {
    @Query("SELECT h FROM HealthyCare h WHERE h.booking.id = :bookingId AND h.service.id = :serviceId")
    HealthyCare findByBookingAndService(@Param("bookingId") Integer bookingId,
                                        @Param("serviceId") Integer serviceId);
}
