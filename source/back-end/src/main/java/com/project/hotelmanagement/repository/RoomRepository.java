package com.project.hotelmanagement.repository;

import com.project.hotelmanagement.models.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer>, JpaSpecificationExecutor<Room> {
    @Query("SELECT r FROM Room r " +
            "JOIN r.type t " +
            "LEFT JOIN Booking b ON b.room.id = r.id " +
            "AND b.status = 'CONFIRMED' " +
            "AND b.checkInDate < :checkOutDate " +
            "AND b.checkOutDate > :checkInDate " +
            "WHERE b.id IS NULL " +
            "AND (:maxOccupancy IS NULL OR :maxOccupancy = 0 OR t.maxOccupancy >= :maxOccupancy)")
    Page<Room> searchRooms(@Param("maxOccupancy") Integer maxOccupancy,
                           @Param("checkInDate") LocalDate checkInDate,
                           @Param("checkOutDate") LocalDate checkOutDate,
                           Pageable pageable);
}
