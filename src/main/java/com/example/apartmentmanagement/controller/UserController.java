package com.example.apartmentmanagement.controller;

import com.cloudinary.Cloudinary;
import com.example.apartmentmanagement.dto.*;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.UserService;
import com.example.apartmentmanagement.serviceImpl.ImageUploadService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @PostMapping("/add")
    public ResponseEntity<Object> addUser(@RequestBody VerifyUserResponseDTO verifyUserResponseDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            AddNewResidentResponseDTO result = userService.addUser(verifyUserResponseDTO);
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", result);
            response.put("message", "Đã duyệt thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * (Admin) Xem list cư dân mới do staff gửi về để xét duyệt
     *
     * @return
     */
    @GetMapping("/list_resident")
    public ResponseEntity<Object> listResident() {
        Map<String, Object> response = new HashMap<>();
        List<VerifyUserResponseDTO> residentList = userService.showAllVerifyUser();

        if (!residentList.isEmpty()) {
            response.put("status", HttpStatus.OK.value());
            response.put("data", residentList);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Không có cư dân nào cần được duyệt");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @GetMapping("/find")
    public ResponseEntity<Object> findAll(String username) {
        List<UserRequestDTO> userRequestDTOS = userService.getUserByFullName(username);
        Map<String, Object> response = new HashMap<>();
        if (userRequestDTOS != null) {
            response.put("status", HttpStatus.OK.value());
            response.put("data", userRequestDTOS);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Không tìm thấy user phù hợp");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByUsernameOrEmail(@RequestParam String query) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.getUserByEmailOrUserName(query);

            if (user != null) {
                UserRequestDTO userDto = userService.getUserById(user.getUserId());
                response.put("status", HttpStatus.OK.value());
                response.put("data", userDto);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", HttpStatus.NOT_FOUND.value());
                response.put("message", "Không tìm thấy user với username hoặc email này");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user_profile")
    public ResponseEntity<Object> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long userId = user.getUserId();
        UserRequestDTO userRequestDto = userService.getUserDTOById(userId);
        Map<String, Object> response = new HashMap<>();
        if (userRequestDto != null) {
            response.put("status", HttpStatus.OK.value());
            response.put("data", userRequestDto);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Không tìm thấy thông tin cư dân này");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        UserRequestDTO user = userService.getUserById(userId);
        if (user != null) {
            response.put("status", HttpStatus.OK.value());
            response.put("data", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Không tìm thấy user này");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/edit_profile")
    public ResponseEntity<Object> updateUserBaseProfile(@RequestBody UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(userRequestDTO.getUserId()).orElse(null);
        Map<String, Object> response = new HashMap<>();
        try {
            UserResponseDTO result = userService.updateUser(userRequestDTO, user);
            response.put("status", HttpStatus.OK.value());
            response.put("data", result);
            response.put("message", "Cập nhật thông tin thành công");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/update_image")
    public ResponseEntity<Object> updateImage(@RequestPart("file") MultipartFile file) {
        String result = imageUploadService.uploadImage(file);
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("data", result);
        response.put("message", "Khởi tạo url thành công");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user_list")
    public ResponseEntity<Object> getUserList() {
        List<UserRequestDTO> dtos = userService.showAllUser();
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
    public ResponseEntity<Object> verifyUser(
            @RequestParam("verificationFormName") String verificationFormName,
            @RequestParam("verificationFormType") int verificationFormType,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("apartmentName") String apartmentName,
            @RequestParam("contractStartDate") String contractStartDateStr,
            @RequestParam(value = "contractEndDate", required = false) String contractEndDateStr,
            @RequestPart("imageFile") List<MultipartFile> imageFiles) {

        // Chuyển đổi String sang LocalDateTime cho ngày bắt đầu
        LocalDateTime contractStartDate = LocalDateTime.parse(contractStartDateStr, DateTimeFormatter.ISO_DATE_TIME);

        // Xử lý ngày kết thúc (có thể null cho chủ hộ)
        LocalDateTime contractEndDate = null;
        if (verificationFormType != 2 && contractEndDateStr != null && !contractEndDateStr.isEmpty()) {
            contractEndDate = LocalDateTime.parse(contractEndDateStr, DateTimeFormatter.ISO_DATE_TIME);
        }

        Map<String, Object> response = new HashMap<>();
        VerifyUserRequestDTO verifyUserDTO = new VerifyUserRequestDTO(
                verificationFormName, verificationFormType, apartmentName, email, phoneNumber,
                contractStartDate, contractEndDate); // contractEndDate có thể là null
        try {
            VerifyUserResponseDTO result = userService.verifyUser(verifyUserDTO, imageFiles);
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", result);
            response.put("message", "Đã lưu thông tin");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}

