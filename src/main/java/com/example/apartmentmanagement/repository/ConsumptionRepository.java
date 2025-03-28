package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Consumption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {
}
