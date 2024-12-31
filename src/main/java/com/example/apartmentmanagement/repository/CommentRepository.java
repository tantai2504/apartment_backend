package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
