package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
