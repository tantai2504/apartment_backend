package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.ReCoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReCoinRepository extends JpaRepository<ReCoin, Long> {

    List<ReCoin> findByUser_UserId(Long userId);
}
