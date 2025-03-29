package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.PostResponseDTO;
import com.example.apartmentmanagement.dto.PostRequestDTO;
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
        List<PostResponseDTO> posts = postService.getPosts();
        Map<String, Object> response = new HashMap<>();
        if (posts.isEmpty()) {
            response.put("message", "Chưa có post nào");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("status", HttpStatus.OK.value());
            response.put("data", posts);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Object> getPostById(@PathVariable Long postId) {
        Map<String, Object> response = new HashMap<>();
        try {
            PostResponseDTO postResponseDTO = postService.getPostById(postId);
            response.put("status", HttpStatus.OK.value());
            response.put("data", postResponseDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<Object> updatePost(@PathVariable Long postId,
                                             @RequestParam ("title") String title,
                                             @RequestParam ("content") String content,
                                             @RequestParam ("price") float price,
                                             @RequestParam ("depositPrice") float depositPrice,
                                             @RequestParam ("depositCheck") String depositCheck,
                                             @RequestParam ("apartmentName") String apartmentName,
                                             @RequestParam ("postType") String postType,
                                             @RequestParam ("userName") String userName,
                                             @RequestPart("imageFile") List<MultipartFile> imageFiles) {
        PostRequestDTO postRequestDTO = new PostRequestDTO(title, content, price, depositCheck, depositPrice, apartmentName, postType, userName);
        Map<String, Object> response = new HashMap<>();
        try {
            PostResponseDTO postResponseDTO = postService.updatePost(postId, postRequestDTO, imageFiles);
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", postResponseDTO);
            response.put("message", "Cập nhật thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/add_post")
    public ResponseEntity<Object> addPost(@RequestParam ("title") String title,
                                          @RequestParam ("content") String content,
                                          @RequestParam ("price") float price,
                                          @RequestParam ("depositCheck") String depositCheck,
                                          @RequestParam ("apartmentName") String apartmentName,
                                          @RequestParam ("postType") String postType,
                                          @RequestParam ("userName") String userName,
                                          @RequestParam ("depositPrice") float depositPrice,
                                          @RequestPart("imageFile") List<MultipartFile> imageFiles) {
        PostRequestDTO postRequestDTO = new PostRequestDTO(title, content, price, depositCheck, depositPrice, apartmentName, postType, userName);
        Map<String, Object> response = new HashMap<>();
        try {
            PostResponseDTO postResponseDTO = postService.createPost(postRequestDTO, imageFiles);
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", postResponseDTO);
            response.put("message", "Khởi tạo thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Object> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.NO_CONTENT.value());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<Object> filterPosts(
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) String areaRange,
            @RequestParam(required = false) String bedrooms,
            @RequestParam(required = false) String sortBy) {

        List<PostResponseDTO> filteredPosts = postService.filterPosts(priceRange, areaRange, bedrooms, sortBy);
        Map<String, Object> response = new HashMap<>();

        if (filteredPosts.isEmpty()) {
            response.put("message", "Không có bài đăng nào phù hợp");
            response.put("status", HttpStatus.OK.value());
        } else {
            response.put("status", HttpStatus.OK.value());
            response.put("data", filteredPosts);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
