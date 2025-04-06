package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE LOWER(u.userName) LIKE LOWER(CONCAT('%', :userName, '%'))")
    List<User> searchByUserName(@Param("userName") String userName);

    User findByUserName(String username);

    @Query("SELECT u FROM User u WHERE u.userName = :input OR u.email = :input")
    User findByUserNameOrEmail(@Param("input") String input);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_apartment (user_id, apartment_id) VALUES (:userId, :apartmentId)", nativeQuery = true)
    void addUserToApartment(@Param("userId") Long userId, @Param("apartmentId") Long apartmentId);

}
