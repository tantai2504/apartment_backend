package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
