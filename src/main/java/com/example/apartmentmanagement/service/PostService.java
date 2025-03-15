package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.PostDTO;
import com.example.apartmentmanagement.dto.PostRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    List<PostDTO> getPosts();

    PostDTO getPostById(Long id);

    PostDTO createPost(PostRequestDTO postDTO, List<MultipartFile> imageFiles);

    PostDTO updatePost(Long id, PostDTO postDTO);

    void deletePost(Long id);
}
