package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.ApprovedResidentDTO;
import com.example.apartmentmanagement.dto.UserDTO;
import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.ApartmentRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.ApartmentService;
import com.example.apartmentmanagement.service.UserService;
import com.example.apartmentmanagement.util.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Override
    public List<User> showAllUser() {
        return userRepository.findAll();
    }

    @Override
    public String addUser(User user, MultipartFile imageFile) {
        if (user.getUserName() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("User name and password must not be null");
        }
        if (!checkUserExisted(user)) {
            return "Da co user nay";
        } else {
            user.setRole("visitor");

            String imgUrl = imageUploadService.uploadImage(imageFile);

            if (imgUrl.equals("image-url")) {
                user.setUserImgUrl("");
            } else {
                user.setUserImgUrl(imgUrl);
            }
            String encryptPass = AESUtil.encrypt(user.getPassword());
            user.setPassword(encryptPass);

            userRepository.save(user);
            return "Dang ky thanh cong";
        }
    }

    @Override
    public boolean checkUserExisted(User user) {
        List<User> userList = userRepository.findAll();
        for (User u : userList) {
            if (user.getUserName().equals(u.getUserName())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public UserDTO getUserDTOById(Long id) {
        UserDTO userDTO = new UserDTO();
        User user = userRepository.findById(id).orElse(null);

        String userName = user.getUserName();
        String email = user.getEmail();
        String phone = user.getPhone();
        String userImgUrl = user.getUserImgUrl();
        String description = user.getDescription();
        String role = user.getRole();

        userDTO = new UserDTO(userName, email, description, phone, userImgUrl, role);

        return userDTO;
    }

    /**
     *
     * ServiceImpl: Cap nhat thong tin nguoi dung
     *
     * @param userId
     * @param user
     * @param file
     * @return
     */
    @Override
    public String updateUser(Long userId, User user, MultipartFile file) {
        User checkUser = getUserById(userId);
        if (checkUser != null) {
            checkUser.setUserName(user.getUserName());
            checkUser.setFullName(user.getFullName());
            checkUser.setEmail(user.getEmail());
            checkUser.setPhone(user.getPhone());
            checkUser.setDescription(user.getDescription());
            checkUser.setAge(user.getAge());
            checkUser.setBirthday(user.getBirthday());
            checkUser.setIdNumber(user.getIdNumber());
            checkUser.setDescription(user.getDescription());
            checkUser.setJob(user.getJob());
            checkUser.setRole(user.getRole());
            String imgUrl = imageUploadService.uploadImage(file);
            if (!imgUrl.equals("image-url")) {
                checkUser.setUserImgUrl(imgUrl);
            }
            userRepository.save(checkUser);
            return "Update thanh cong";
        } else {
            return "Update that bai";
        }
    }

    @Override
    public User getUserByName(String name) {
        User user = userRepository.findByUserName(name);
        return user;
    }

    @Override
    public String fillUserBaseInfo(ApprovedResidentDTO approvedResidentDTO) {

//        Long userId = approvedResidentDTO.getUserId();
//        String apartmentName = approvedResidentDTO.getApartmentName();
//        Resident resident = approvedResidentDTO.getResident();
//
//        User user = getUserById(userId);
//        Apartment apartment = apartmentService.getApartmentByName(apartmentName);
//
//        resident.setApartment(apartment);
//        resident.setFullName(user.getFullName());
//        resident.setUser(user);
//
//        if (user.getRole().equals("resident")) {
//            apartment.setHouseholder(user.getFullName());
//            apartment.setUser(user);
//            apartment.setStatus("rented");
//            int currentNumber = apartment.getTotalNumber();
//            int totalNumber = currentNumber + 1;
//            apartment.setTotalNumber(totalNumber);
//            apartmentRepository.save(apartment);
//        }
//        residentRepository.save(resident);
        return "Success";
    }

//    @Override
//    public boolean approvedUser(List<ApprovedResidentDTO> approvedResidentDTO) {
//        Long userId;
//        String apartmentName;
//        for(ApprovedResidentDTO a: approvedResidentDTO) {
//            Resident resident = a.getResident();
//            userId = a.getUserId();
//            apartmentName = a.getApartmentName();
//            User user = getUserById(userId);
//            Apartment apartment = apartmentService.getApartmentByName(apartmentName);
//            user.setRole("visitor");
//            updateUserBecomeResident(user, resident, apartment);
//            userRepository.save(user);
//        }
//        return true;
//    }

//    private void updateUserBecomeResident(User user, Resident resident, Apartment apartment){
//        resident.setApartment(apartment);
//        resident.setUser(user);
//        residentRepository.save(resident);
//    }
}
