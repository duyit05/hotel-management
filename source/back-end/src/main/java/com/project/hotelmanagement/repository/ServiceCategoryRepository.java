package com.project.hotelmanagement.repository;

import com.project.hotelmanagement.models.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Integer> {
}
