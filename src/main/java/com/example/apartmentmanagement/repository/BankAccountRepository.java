package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long>{
    BankAccount findByUser_UserId(Long userId);
}
