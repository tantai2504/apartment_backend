package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.*;
import com.example.apartmentmanagement.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    List<UserRequestDTO> showAllUser();

    AddNewResidentResponseDTO addUser(VerifyUserResponseDTO newAccountDTO);

    RegisterResponseDTO register(VerifyOTPRequestDTO registerRequestDTO);

    VerifyRegisterRequestDTO verifyRegister(RegisterRequestDTO verifyRegisterRequestDTO);

    UserRequestDTO getUserById (Long id);

    UserRequestDTO getUserDTOById (Long id);

    boolean updateImage(User user, MultipartFile imageFile);

    UserResponseDTO updateUser(UserRequestDTO userRequestDTO, User user);

    User getUserByName(String name);

    List<UserRequestDTO> getUserByFullName(String fullName);

    void deleteUserById(Long apartmentId, Long userId);

    void setCurrentStatusForApartment(Long apartmentId);

    User getUserByEmailOrUserName(String email);

    VerifyUserResponseDTO verifyUser(VerifyUserRequestDTO verifyUserDTO, List<MultipartFile> imageFile);

    void saveUser(User user);

    void removeUserVerificationForm(Long verificationFormId);

    List<VerifyUserResponseDTO> showAllVerifyUser();

}
