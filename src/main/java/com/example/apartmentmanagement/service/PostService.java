package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.PostDTO;
import com.example.apartmentmanagement.dto.PostRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    List<PostDTO> getPosts();

    PostDTO getPostById(Long id);

    PostDTO createPost(PostRequestDTO postRequestDTO, List<MultipartFile> imageFiles);

    PostDTO updatePost(Long id, PostRequestDTO postRequestDTO, List<MultipartFile> imageFiles);

    void deletePost(Long id);
}
