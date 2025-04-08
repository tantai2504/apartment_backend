package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.Consumption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {
    List<Consumption> findByApartment(Apartment apartment);
    Consumption findTopByApartmentAndConsumptionDateBetweenOrderByConsumptionDateDesc(Apartment apartment, LocalDate start, LocalDate end);
}
