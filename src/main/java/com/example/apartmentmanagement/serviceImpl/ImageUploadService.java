package com.example.apartmentmanagement.serviceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

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
}
