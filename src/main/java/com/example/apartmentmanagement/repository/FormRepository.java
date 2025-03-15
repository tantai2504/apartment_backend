package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    List<Form> findByUserUserId(Long userId);
    List<Form> findByFormType(String formType);
}