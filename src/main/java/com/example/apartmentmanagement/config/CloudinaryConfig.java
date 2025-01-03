package com.example.apartmentmanagement.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dct0qbbjc",
                "api_key", "463646923542281",
                "api_secret", "tB2WCK2Bjl3lJ-Q7uDmdEM_PFYA"));
    }
}
