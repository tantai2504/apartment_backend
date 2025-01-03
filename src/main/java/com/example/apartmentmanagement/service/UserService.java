package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.entities.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    String addUser(User user, MultipartFile imageFile);

    boolean checkUserExisted(User user);

    User getUserById (Long id);
}
