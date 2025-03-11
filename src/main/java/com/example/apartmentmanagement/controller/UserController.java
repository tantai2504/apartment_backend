package com.example.apartmentmanagement.controller;

import com.cloudinary.Cloudinary;
import com.example.apartmentmanagement.dto.UserDTO;
import com.example.apartmentmanagement.dto.VerifyUserDTO;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<Object> addUser(
            @Valid @ModelAttribute User user,
            @RequestPart(value = "file", required = false) MultipartFile imageFile,
            @RequestParam(value = "apartment_id", required = false) Long apartmentId,
            @RequestParam(value = "fullName") String verificationOwner) {
        String result = userService.addUser(user, imageFile, apartmentId, verificationOwner);
        if (result.equals("Add Successfully")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
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
    public ResponseEntity<String> verifyUser(VerifyUserDTO verifyUserDTO, @RequestPart("imageFile") List<MultipartFile> imageFiles){
        String result = userService.verifyUser(verifyUserDTO, imageFiles);
        if (result.equals("success")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Có lỗi xảy ra trong quá trình phê duyệt");
        }
    }
}

