package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Post;
import com.example.apartmentmanagement.entities.PostImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImagesRepository extends JpaRepository<PostImages, Long> {
    List<PostImages> findByPost(Post post);

    void deleteByPost(Post post);
}
