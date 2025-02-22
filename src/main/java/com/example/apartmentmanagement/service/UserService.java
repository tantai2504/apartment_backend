package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.ApprovedResidentDTO;
import com.example.apartmentmanagement.dto.UserDTO;
import com.example.apartmentmanagement.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    List<User> showAllUser();

    String addUser(User user, MultipartFile imageFile, Long apartmentId);

    boolean checkUserExisted(User user);

    User getUserById (Long id);

    UserDTO getUserDTOById (Long id);

//    boolean approvedUser(List<ApprovedResidentDTO> approvedResidentDTO);

    String updateUser(Long userId, User user, MultipartFile file);

    User getUserByName(String name);

    // Điền thông tin chi tiết để gửi lại cho phía admin sau khi nhận được đơn yêu cầu điền thông tin từ admin
    // Diễn ra sau khi đã hoàn tất quá trình đặt cọc, booking ký hợp đồng
    String fillUserBaseInfo(ApprovedResidentDTO approvedResidentDTO);

}
