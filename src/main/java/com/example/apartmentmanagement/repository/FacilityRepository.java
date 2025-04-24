package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Facility;
import com.example.apartmentmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
    List<Facility> findByUser(User user);
}
