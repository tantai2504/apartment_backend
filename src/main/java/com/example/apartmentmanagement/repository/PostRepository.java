package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByPriceLessThan(float price);

    boolean existsByApartment_ApartmentNameAndPostType(String apartmentName, String postType);
}
