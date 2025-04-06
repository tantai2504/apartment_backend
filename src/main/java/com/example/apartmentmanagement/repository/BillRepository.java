package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Bill;
import com.example.apartmentmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findBillByUser(User user);
}
