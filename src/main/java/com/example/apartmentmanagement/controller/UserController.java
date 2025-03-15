package com.example.apartmentmanagement.controller;

import com.cloudinary.Cloudinary;
import com.example.apartmentmanagement.dto.CreateNewAccountDTO;
import com.example.apartmentmanagement.dto.UserDTO;
import com.example.apartmentmanagement.dto.VerifyUserRequestDTO;
import com.example.apartmentmanagement.dto.VerifyUserResponseDTO;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<Object> findAll(String username) {
        List<UserDTO> userDTOS = userService.getUserByFullName(username);
        Map<String, Object> response = new HashMap<>();
        if (userDTOS != null) {
            response.put("status", HttpStatus.OK.value());
            response.put("data", userDTOS);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Không tìm thấy user phù hợp");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/user_profile")
    public ResponseEntity<Object> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getUserId();
        UserDTO userDto = userService.getUserDTOById(userId);
        Map<String, Object> response = new HashMap<>();
        if (userDto != null) {
            response.put("status", HttpStatus.OK.value());
            response.put("data", userDto);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Không tìm thấy thông tin cư dân này");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/edit_profile")
    public ResponseEntity<Object> updateUserBaseProfile(@RequestBody UserDTO userDTO, HttpSession session) {
        User user = (User) session.getAttribute("user");
        String result = userService.updateUser(userDTO, user);
        Map<String, Object> response = new HashMap<>();
        if (result.equals("Update thành công")) {
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", userDTO);
            response.put("message", result);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Lỗi khi cập nhật dữ liệu");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/update_image")
    public ResponseEntity<Object> updateImage(@RequestPart("file") MultipartFile file, HttpSession session) {
        User user = (User) session.getAttribute("user");
        boolean result = userService.updateImage(user, file);
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", user.getUserImgUrl());
            response.put("message", "Update ảnh thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Update ảnh thất bại");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/user_list")
    public ResponseEntity<Object> getUserList() {
        List<UserDTO> dtos = userService.showAllUser();
        Map<String, Object> response = new HashMap<>();
        if(dtos != null) {
            response.put("status", HttpStatus.OK.value());
            response.put("data", dtos);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Không có user nào");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
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
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Lỗi");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}

