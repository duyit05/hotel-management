package com.project.hotelmanagement.repository;

import com.project.hotelmanagement.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :start AND b.createdAt < :end")
    int countByDateBooking(@Param("start") Date start, @Param("end") Date end);

    @Query("SELECT COUNT(b) FROM Booking b " +
            "WHERE b.room.id = :roomId " +
            "AND b.status = 'CONFIRMED' " +
            "AND (:newCheckIn < b.checkOutDate) " +
            "AND (:newCheckOut > b.checkInDate)" )
    int countOverLappingBookings (@Param("roomId") int roomId,
                                  @Param("newCheckIn")LocalDate newCheckIn,
                                  @Param("newCheckOut") LocalDate newCheckOut);

    @Query("SELECT b FROM Booking b WHERE b.status = 'CONFIRMED' OR b.status = 'PENDING'")
    List<Booking> findBookingsByStatus(Integer bookingId);
}
