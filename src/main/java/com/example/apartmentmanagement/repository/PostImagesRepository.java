package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Post;
import com.example.apartmentmanagement.entities.PostImages;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface PostImagesRepository extends JpaRepository<PostImages, Long> {
    List<PostImages> findByPost(Post post);
}
