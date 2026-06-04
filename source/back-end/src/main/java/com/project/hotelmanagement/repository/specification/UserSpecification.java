package com.project.hotelmanagement.repository.specification;

import com.project.hotelmanagement.enums.GenderType;
import com.project.hotelmanagement.enums.UserRank;
import com.project.hotelmanagement.enums.UserStatus;
import com.project.hotelmanagement.models.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), pattern),
                    cb.like(cb.lower(root.get("lastName")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern),
                    cb.like(cb.lower(root.get("username")), pattern)
            );
        };
    }

    public static Specification<User> hasStatus(UserStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<User> hasRank(UserRank rank) {
        return (root, query, cb) ->
                rank == null ? null : cb.equal(root.get("rank"), rank);
    }

    public static Specification<User> hasGender(GenderType gender) {
        return (root, query, cb) ->
                gender == null ? null : cb.equal(root.get("gender"), gender);
    }
}
