package com.example.apartmentmanagement.controller;

import com.cloudinary.Cloudinary;
import com.example.apartmentmanagement.dto.CreateNewAccountDTO;
import com.example.apartmentmanagement.dto.UserDTO;
import com.example.apartmentmanagement.dto.VerifyUserRequestDTO;
import com.example.apartmentmanagement.dto.VerifyUserResponseDTO;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.entities.VerificationForm;
import com.example.apartmentmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private Cloudinary cloudinary;

    @PostMapping("/add")
    public ResponseEntity<Object> addUser(@RequestBody CreateNewAccountDTO newAccountDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            CreateNewAccountDTO result = userService.addUser(newAccountDTO);
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", result);
            response.put("message", "Đã cấp tài khoản thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/find")
    public ResponseEntity<List<UserDTO>> findAll(String username) {
        List<UserDTO> userDTOS = userService.getUserByFullName(username);
        return ResponseEntity.ok(userDTOS);
    }

    @GetMapping("/user_profile")
    public ResponseEntity<Object> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getUserId();
        
        UserDTO userDto = userService.getUserDTOById(userId);
        if (userDto != null) {
            return ResponseEntity.ok(userDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/edit_profile")
    public ResponseEntity<Object> updateUserBaseProfile(@RequestBody UserDTO userDTO, HttpSession session) {
        User user = (User) session.getAttribute("user");
        String result = userService.updateUser(userDTO, user);
        if (result.equals("Update thành công")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Cập nhật thành công!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Cập nhật thất bại!"));
        }
    }

    @PutMapping("/update_image")
    public ResponseEntity<Object> updateImage(@RequestPart("file") MultipartFile file, HttpSession session) {
        User user = (User) session.getAttribute("user");
        boolean result = userService.updateImage(user, file);
        if (result) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Cập nhật ảnh thành công!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Cập nhật ảnh thất bại!"));
        }
    }

    @GetMapping("/user_list")
    public ResponseEntity<Object> getUserList() {
        List<UserDTO> dtos = userService.showAllUser();
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteUser(Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify_user")
    public ResponseEntity<Object> verifyUser(@RequestParam("verificationFormName") String verificationFormName,
                                             @RequestParam("fullName") String fullName,
                                             @RequestParam("email") String email,
                                             @RequestParam("phoneNumber") String phoneNumber,
                                             @RequestParam("contractStartDate") LocalDateTime contractStartDate,
                                             @RequestParam("contractEndDate") LocalDateTime contractEndDate,
                                             @RequestPart("imageFile") List<MultipartFile> imageFiles){
        Map<String, Object> response = new HashMap<>();
        VerifyUserRequestDTO verifyUserDTO = new VerifyUserRequestDTO(
                verificationFormName, fullName, email, phoneNumber, contractStartDate, contractEndDate);
        try {
            VerifyUserResponseDTO result = userService.verifyUser(verifyUserDTO, imageFiles);
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", result);
            response.put("message", "Đã lưu thông tin");
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Lỗi");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}

