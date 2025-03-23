package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.*;
import com.example.apartmentmanagement.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    List<UserDTO> showAllUser();

    AddNewResidentResponseDTO addUser(VerifyUserResponseDTO newAccountDTO);

    RegisterResponseDTO register(VerifyOTPRequestDTO registerRequestDTO);

    VerifyRegisterRequestDTO verifyRegister(RegisterRequestDTO verifyRegisterRequestDTO);

    UserDTO getUserById (Long id);

    UserDTO getUserDTOById (Long id);

    boolean updateImage(User user, MultipartFile imageFile);

    String updateUser(UserDTO userDTO, User user);

    User getUserByName(String name);

    List<UserDTO> getUserByFullName(String fullName);

    String deleteUserById(Long id);

    User getUserByEmailOrUserName(String email);

    VerifyUserResponseDTO verifyUser(VerifyUserRequestDTO verifyUserDTO, List<MultipartFile> imageFile);

    void saveUser(User user);

    List<VerifyUserResponseDTO> showAllVerifyUser();

}
