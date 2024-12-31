package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
