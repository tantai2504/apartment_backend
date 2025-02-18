package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String username);
}
