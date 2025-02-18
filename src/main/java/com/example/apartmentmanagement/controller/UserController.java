package com.example.apartmentmanagement.controller;

import com.cloudinary.Cloudinary;
import com.example.apartmentmanagement.dto.ApprovedResidentDTO;
import com.example.apartmentmanagement.dto.UserDTO;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.service.UserService;
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
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        String result = userService.addUser(user, imageFile);
        if (result.equals("Add Successfully")) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Add successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    @PostMapping("/approve")
    public ResponseEntity<String> fillUserBaseInfo(
            @RequestBody ApprovedResidentDTO approvedResidentDTO) {

        String result = userService.fillUserBaseInfo(approvedResidentDTO);

        if (result == "Success") {
            return ResponseEntity.ok("User và Resident đã được cập nhật thành công!");
        }

        return ResponseEntity.badRequest().body("Có lỗi xảy ra khi cập nhật thông tin!");
    }

    @GetMapping("/user_profile")
    public ResponseEntity<Object> getUserInfo(@RequestParam(value="userId") Long userId) {
        UserDTO user = userService.getUserDTOById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/edit_profile")
    public ResponseEntity<Object> updateUserBaseProfile(
            @Valid @ModelAttribute User user,
            @RequestPart(value = "file", required = false) MultipartFile imageFile,
            @RequestParam(value = "userId") Long userId,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        String result = userService.updateUser(userId, user, imageFile);
        if (result.equals("Update Successfully")) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Update Successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }


}
