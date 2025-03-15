package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
