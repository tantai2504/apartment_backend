package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.UserDTO;
import com.example.apartmentmanagement.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    List<UserDTO> showAllUser();

    String addUser(User user, MultipartFile imageFile, Long apartmentId);

    boolean checkUserExisted(User user);

    User getUserById (Long id);

    UserDTO getUserDTOById (Long id);

    boolean updateImage(User user, MultipartFile imageFile);

    String updateUser(UserDTO userDTO, User user);

    User getUserByName(String name);

    User getUserByFullName(String fullName);

}
