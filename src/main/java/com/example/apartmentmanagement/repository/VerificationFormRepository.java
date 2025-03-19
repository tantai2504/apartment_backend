package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.VerificationForm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationFormRepository extends JpaRepository<VerificationForm, Long> {
    VerificationForm findVerificationFormByUserNameContainingIgnoreCase(String fullName);
}
