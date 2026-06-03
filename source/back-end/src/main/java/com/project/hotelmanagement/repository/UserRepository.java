package com.project.hotelmanagement.repository;

import com.project.hotelmanagement.enums.GenderType;
import com.project.hotelmanagement.enums.StatusChat;
import com.project.hotelmanagement.enums.UserRank;
import com.project.hotelmanagement.enums.UserStatus;
import com.project.hotelmanagement.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername (String username);
    Boolean existsByUsername (String username);

    List<User> findAlByChatStatus(StatusChat statusChat);

    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR u.status = :status) " +
            "AND (:rank IS NULL OR u.rank = :rank) " +
            "AND (:gender IS NULL OR u.gender = :gender)")
    Page<User> searchUsers(
            @Param("keyword") String keyword,
            @Param("status") UserStatus status,
            @Param("rank") UserRank rank,
            @Param("gender") GenderType gender,
            Pageable pageable
    );
}
