package com.project.hotelmanagement.repository;

import com.project.hotelmanagement.models.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceItem, Integer> {
}
