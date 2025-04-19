package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
}
