package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserResponseDTO {
    private Long userId;
    private String userName;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String description;
    private String userImgUrl;
    private String age;
    private LocalDate birthday;
    private String idNumber;
    private String job;
    private boolean isRentor;
    private Float accountBallance;

//    dto.put("userId", user.getUserId());
//            dto.put("userName", user.getUserName());
//            dto.put("password", user.getPassword());
//            dto.put("fullName", user.getFullName());
//            dto.put("email", user.getEmail());
//            dto.put("phone", user.getPhone());
//            dto.put("role", user.getRole());
//            dto.put("description", user.getDescription());
//            dto.put("userImgUrl", user.getUserImgUrl());
//            dto.put("age", user.getAge());
//            dto.put("birthday", user.getBirthday());
//            dto.put("idNumber", user.getIdNumber());
//            dto.put("job", user.getJob());
//            if(user.isRentor()){
//        dto.put("isRentor",true);
//    }else{
//        dto.put("isRentor",false);
//    }
//            dto.put("accountBallance", user.getAccountBalance());
}
