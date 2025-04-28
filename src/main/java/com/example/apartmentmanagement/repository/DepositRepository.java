package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<Deposit, Long> {

    Deposit findDepositByUser_UserId(Long userId);
}
