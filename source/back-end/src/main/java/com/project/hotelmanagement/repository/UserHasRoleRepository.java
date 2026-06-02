package com.project.hotelmanagement.repository;

import com.project.hotelmanagement.models.Role;
import com.project.hotelmanagement.models.User;
import com.project.hotelmanagement.models.UserHasRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHasRoleRepository extends JpaRepository<UserHasRole, Integer> {
    Boolean existsByUserAndRole(User user, Role role);
}
