package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.UserService;
import com.example.apartmentmanagement.util.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Override
    public String addUser(User user, MultipartFile imageFile) {
        if (user.getUserName() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("User name and password must not be null");
        }
        if (!checkUserExisted(user)) {
            return "Da co user nay";
        } else {
            user.setRole("visitor");

            String imgUrl = imageUploadService.uploadImage(imageFile);

            if (imgUrl.equals("image-url")) {
                user.setUserImgUrl("");
            } else {
                user.setUserImgUrl(imgUrl);
            }
            String encryptPass = AESUtil.encrypt(user.getPassword());
            user.setPassword(encryptPass);

            userRepository.save(user);
            return "Dang ky thanh cong";
        }
    }

    @Override
    public boolean checkUserExisted(User user) {
        List<User> userList = userRepository.findAll();
        for (User u : userList) {
            if (user.getUserName().equals(u.getUserName())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
