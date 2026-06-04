package com.project.hotelmanagement.repository;

import com.project.hotelmanagement.enums.GenderType;
import com.project.hotelmanagement.enums.StatusChat;
import com.project.hotelmanagement.enums.UserRank;
import com.project.hotelmanagement.enums.UserStatus;
import com.project.hotelmanagement.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername (String username);
    Boolean existsByUsername (String username);

    List<User> findAlByChatStatus(StatusChat statusChat);
}
