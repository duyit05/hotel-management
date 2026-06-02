package com.project.hotelmanagement.repository;

import com.project.hotelmanagement.dto.response.UserResponse;
import com.project.hotelmanagement.enums.StatusChat;
import com.project.hotelmanagement.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername (String username);
    Boolean existsByUsername (String username);

    List<User> findAlByChatStatus(StatusChat statusChat);
}
