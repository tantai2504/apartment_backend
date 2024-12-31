package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Long> {
}
