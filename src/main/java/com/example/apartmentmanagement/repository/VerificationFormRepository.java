package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.VerificationForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationFormRepository extends JpaRepository<VerificationForm, Long> {
    VerificationForm findVerificationFormByUserNameContainingIgnoreCase(String fullName);
    List<VerificationForm> findByApartmentNameIgnoreCaseAndVerified(String apartmentName, boolean verified);

    List<VerificationForm> findByApartmentNameIgnoreCaseAndVerifiedAndVerificationFormType(
            String apartmentName,
            boolean verified,
            int verificationFormType
    );

    List<VerificationForm> findByContractEndDateBetweenAndVerifiedIsTrue(LocalDateTime start, LocalDateTime end);

    List<VerificationForm> findByContractEndDateBeforeAndVerifiedIsTrueAndExpiredIsFalseAndVerificationFormType(
            LocalDateTime date,
            int formType
    );
}