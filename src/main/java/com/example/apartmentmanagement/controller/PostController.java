package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.PostDTO;
import com.example.apartmentmanagement.dto.PostRequestDTO;
import com.example.apartmentmanagement.entities.Post;
import com.example.apartmentmanagement.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<Object> getPosts() {
        List<PostDTO> posts = postService.getPosts();
        Map<String, Object> response = new HashMap<>();
        if (posts.isEmpty()) {
            response.put("message", "Người dùng này không có post nào");
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            response.put("status", HttpStatus.OK.value());
            response.put("posts", posts);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Object> getPostById(@PathVariable Long postId) {
        PostDTO postDTO = postService.getPostById(postId);
        Map<String, Object> response = new HashMap<>();
        if (postDTO == null) {
            response.put("message", "Không tìm thấy post này");
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            response.put("status", HttpStatus.OK.value());
            response.put("post", postDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @PostMapping("/add_post")
    public ResponseEntity<Object> addPost(@RequestParam ("title") String title,
                                          @RequestParam ("content") String content,
                                          @RequestParam ("depositCheck") boolean depositCheck,
                                          @RequestParam ("postType") String postType,
                                          @RequestParam ("userName") String userName,
                                          @RequestPart("imageFile") List<MultipartFile> imageFiles) {
        PostRequestDTO postRequestDTO = new PostRequestDTO(title, content, depositCheck, postType, userName);
        Map<String, Object> response = new HashMap<>();
        try {
            PostDTO postDTO = postService.createPost(postRequestDTO, imageFiles);
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", postDTO);
            response.put("message", "Khởi tạo thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
