package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResidentRepository extends JpaRepository<Resident, Long> {

    @Query("SELECT COUNT(r) FROM Resident r WHERE r.apartment.id = :apartmentId")
    int countResidentsByApartmentId(Long apartmentId);

}
