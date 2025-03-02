package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE LOWER(u.userName) LIKE LOWER(CONCAT('%', :userName, '%'))")
    List<User> searchByUserName(@Param("userName") String userName);

    User findByUserName(String username);

    User findByFullName(String fullName);
}
