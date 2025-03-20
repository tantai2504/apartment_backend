package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.dto.ApartmentDTO;
import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    List<Apartment> findApartmentByApartmentNameContaining (String name);
    Apartment findApartmentByApartmentName (String name);
}
