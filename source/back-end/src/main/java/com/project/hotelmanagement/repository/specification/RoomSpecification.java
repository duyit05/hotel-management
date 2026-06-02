package com.project.hotelmanagement.repository.specification;

import com.project.hotelmanagement.models.Room;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class RoomSpecification {
    public static Specification<Room> hasName (String name){
        return new Specification<Room>() {
            @Override
            public Predicate toPredicate(Root<Room> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return null;
            }
        };
    }
}
