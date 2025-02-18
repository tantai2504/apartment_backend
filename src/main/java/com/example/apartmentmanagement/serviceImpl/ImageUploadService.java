package com.example.apartmentmanagement.serviceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ImageUploadService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return "image-url";
        }
        try {
            Map<String, Object> uploadResult = cloudinary.uploader()
                    .upload(imageFile.getBytes(), ObjectUtils.emptyMap());
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            throw new RuntimeException("Upload ảnh thất bại: " + e.getMessage(), e);
        }
    }

    public List<String> uploadMultipleImages(List<MultipartFile> imageFileList) {
        List<String> imageUrls = new ArrayList<>();
        if (imageFileList == null || imageFileList.isEmpty()) {
            return Collections.emptyList(); // Trả về danh sách rỗng nếu không có ảnh
        }
        for (MultipartFile imageFile : imageFileList) {
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    Map<String, Object> uploadResult = cloudinary.uploader()
                            .upload(imageFile.getBytes(), ObjectUtils.emptyMap());
                    imageUrls.add((String) uploadResult.get("url"));
                } catch (IOException e) {
                    throw new RuntimeException("Upload ảnh thất bại: " + e.getMessage(), e);
                }
            }
        }
        return imageUrls;
    }
}

