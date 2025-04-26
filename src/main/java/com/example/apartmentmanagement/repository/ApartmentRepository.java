package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    List<Apartment> findApartmentByApartmentNameContaining (String name);
    Apartment findApartmentByApartmentName (String name);
    @Query(value = "SELECT a.* FROM apartment a " +
            "JOIN user_apartment ua ON a.apartment_id = ua.apartment_id " +
            "WHERE ua.user_id = :userId", nativeQuery = true)
    List<Apartment> findApartmentsByRenterId(Long userId);
    List<Apartment> findApartmentByHouseholder(String householder);
}
