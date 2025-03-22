package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.PostResponseDTO;
import com.example.apartmentmanagement.dto.PostRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    List<PostResponseDTO> getPosts();

    PostResponseDTO getPostById(Long id);

    PostResponseDTO createPost(PostRequestDTO postRequestDTO, List<MultipartFile> imageFiles);

    PostResponseDTO updatePost(Long id, PostRequestDTO postRequestDTO, List<MultipartFile> imageFiles);

    void deletePost(Long id);

    List<PostResponseDTO> filterPosts(String priceRange, String sortBy);
}
