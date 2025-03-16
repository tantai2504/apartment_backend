package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.PostDTO;
import com.example.apartmentmanagement.dto.PostRequestDTO;
import com.example.apartmentmanagement.entities.Post;
import com.example.apartmentmanagement.entities.PostImages;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.PostImagesRepository;
import com.example.apartmentmanagement.repository.PostRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostImagesRepository postImagesRepository;

    @Autowired
    private PostRepository postRepository;

    @Override
    public List<PostDTO> getPosts() {
        return postRepository.findAll().stream().map(post -> new PostDTO(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.isDepositCheck(),
                post.getPostType(),
                post.getPostDate(),
                post.getUser().getUserName(),
                post.getPostImages().stream().map(PostImages::getPostImagesUrl).toList()
                ))
        .collect(Collectors.toList());
    }

    @Override
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id).get();
        PostDTO dto = new PostDTO(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.isDepositCheck(),
                post.getPostType(),
                post.getPostDate(),
                post.getUser().getUserName(),
                post.getPostImages().stream().map(PostImages::getPostImagesUrl).toList()
        );
        return dto;
    }

    @Override
    public PostDTO createPost(PostRequestDTO postDTO, List<MultipartFile> imageFiles) {
        Post post = new Post();

        if (postDTO == null) {
            throw new RuntimeException("Dữ liệu bài đăng không được để trống");
        }

        if (postDTO.getTitle() == null || postDTO.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Tiêu đề bài đăng không được để trống");
        }

        if (postDTO.getPostType() == null) {
            throw new RuntimeException("Loại bài đăng không được để trống");
        }

        if (postDTO.getContent() == null || postDTO.getContent().trim().isEmpty()) {
            throw new RuntimeException("Nội dung bài đăng không được để trống");
        }

        if (postDTO.getUserName() == null || postDTO.getUserName().trim().isEmpty()) {
            throw new RuntimeException("Tên người dùng không được để trống");
        }

        post.setTitle(postDTO.getTitle());
        post.setPostType(postDTO.getPostType());
        post.setPostDate(LocalDateTime.now());
        post.setDepositCheck(postDTO.isDepositCheck());
        post.setContent(postDTO.getContent());

        User user = userRepository.findByUserNameOrEmail(postDTO.getUserName());
        if (user == null) {
            throw new RuntimeException("Không tìm thấy username này");
        }
        post.setUser(user);

        List<String> postImagesUrl = imageUploadService.uploadMultipleImages(imageFiles);
        List<PostImages> postImagesList = new ArrayList<>();

        for (String imageUrl : postImagesUrl) {
            PostImages postImages = new PostImages();
            postImages.setPostImagesUrl(imageUrl);
            postImages.setPost(post);
            postImagesList.add(postImages);
        }

        post.setPostImages(postImagesList);

        postRepository.save(post);
        postImagesRepository.saveAll(postImagesList);

        PostDTO dto = new PostDTO(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.isDepositCheck(),
                post.getPostType(),
                post.getPostDate(),
                post.getUser().getUserName(),
                post.getPostImages().stream().map(PostImages::getPostImagesUrl).toList()
        );
        return dto;
    }

    @Override
    public PostDTO updatePost(Long id, PostRequestDTO postDTO, List<MultipartFile> imageFiles) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng với ID: " + id));

        if (postDTO == null) {
            throw new RuntimeException("Dữ liệu bài đăng không được để trống");
        }
        if (postDTO.getTitle() == null || postDTO.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Tiêu đề bài đăng không được để trống");
        }
        if (postDTO.getPostType() == null) {
            throw new RuntimeException("Loại bài đăng không được để trống");
        }
        if (postDTO.getContent() == null || postDTO.getContent().trim().isEmpty()) {
            throw new RuntimeException("Nội dung bài đăng không được để trống");
        }
        if (postDTO.getUserName() == null || postDTO.getUserName().trim().isEmpty()) {
            throw new RuntimeException("Tên người dùng không được để trống");
        }

        post.setTitle(postDTO.getTitle());
        post.setPostType(postDTO.getPostType());
        post.setPostDate(LocalDateTime.now());
        post.setDepositCheck(postDTO.isDepositCheck());
        post.setContent(postDTO.getContent());

        User user = userRepository.findByUserNameOrEmail(postDTO.getUserName());
        if (user == null) {
            throw new RuntimeException("Không tìm thấy username này");
        }
        post.setUser(user);

        if (imageFiles != null && !imageFiles.isEmpty()) {
            post.getPostImages().clear();

            List<String> postImagesUrl = imageUploadService.uploadMultipleImages(imageFiles);
            List<PostImages> postImagesList = postImagesUrl.stream().map(url -> {
                PostImages postImage = new PostImages();
                postImage.setPostImagesUrl(url);
                postImage.setPost(post);
                return postImage;
            }).toList();

            post.getPostImages().addAll(postImagesList);
        }


        postRepository.save(post);

        return new PostDTO(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.isDepositCheck(),
                post.getPostType(),
                post.getPostDate(),
                post.getUser().getUserName(),
                post.getPostImages().stream().map(PostImages::getPostImagesUrl).toList()
        );
    }


    @Override
    public void deletePost(Long id) {

    }
}
