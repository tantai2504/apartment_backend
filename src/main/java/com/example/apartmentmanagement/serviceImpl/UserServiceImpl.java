package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.UserDTO;
import com.example.apartmentmanagement.dto.VerifyUserDTO;
import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.ContractImages;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.entities.VerificationForm;
import com.example.apartmentmanagement.repository.ApartmentRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.repository.VerificationFormRepository;
import com.example.apartmentmanagement.service.UserService;
import com.example.apartmentmanagement.util.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private VerificationFormRepository verificationFormRepository;

    @Override
    public List<UserDTO> showAllUser() {
        return userRepository.findAll().stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setUserName(user.getUserName());
            dto.setFullName(user.getFullName());
            dto.setPassword(null);
            dto.setEmail(user.getEmail());
            dto.setDescription(user.getDescription());
            dto.setPhone(user.getPhone());
            dto.setUserImgUrl(user.getUserImgUrl());
            dto.setAge(user.getAge());
            dto.setBirthday(user.getBirthday());
            dto.setIdNumber(user.getIdNumber());
            dto.setJob(user.getJob());
            dto.setRole(user.getRole());
            dto.setApartmentName(user.getApartment() != null ? user.getApartment().getApartmentName() : null);
            return dto;
        }).toList();
    }

    @Override
    public String addUser(User user, MultipartFile imageFile, Long apartmentId, String verificationOwner) {
        if (user.getUserName() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("User name and password must not be null");
        }
        if (!checkUserExisted(user)) {
            return "Da co user nay";
        } else {
            String imgUrl = imageUploadService.uploadImage(imageFile);

            if (imgUrl.equals("image-url")) {
                user.setUserImgUrl("");
            } else {
                user.setUserImgUrl(imgUrl);
            }
            String encryptPass = AESUtil.encrypt(user.getPassword());
            user.setPassword(encryptPass);

            Apartment apartment = apartmentRepository.findById(apartmentId).get();

            apartment.setTotalNumber(apartment.getTotalNumber() + 1);
            if (apartment.getTotalNumber()>0) {
                apartment.setStatus("rented");
            }
            if (apartment.getHouseholder() == null) {
                if (user.getRole().equals("Chủ sở hữu")){
                    apartment.setHouseholder(user.getFullName());
                }
            } else {
                if (user.getRole().equals("Chủ sở hữu")){
                    return "Da co nguoi dung ten chu can ho";
                }
            }

            VerificationForm verificationForm = verificationFormRepository.findVerificationFormByFullNameEqualsIgnoreCase(verificationOwner);

            user.setVerificationForm(verificationForm);

            user.setApartment(apartment);

            userRepository.save(user);

            return "Add Successfully";
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
        UserDTO userDTO;
        User user = userRepository.findById(id).orElse(null);
        userDTO = new UserDTO(
                user.getUserName(),
                user.getFullName(),
                user.getPassword(),
                user.getEmail(),
                user.getDescription(),
                user.getPhone(),
                user.getUserImgUrl(),
                user.getAge(),
                user.getBirthday(),
                user.getIdNumber(),
                user.getJob(),
                user.getApartment().getApartmentName(),
                user.getRole()
        );
        return userDTO;
    }

    @Override
    public boolean updateImage(User user, MultipartFile imageFile) {
        String imgUrl = imageUploadService.uploadImage(imageFile);
        if (imgUrl.equals("image-url")) {
            user.setUserImgUrl("");
        } else {
            user.setUserImgUrl(imgUrl);
        }
        userRepository.save(user);
        return true;
    }

    /**
     * ServiceImpl: Cap nhat thong tin nguoi dung
     *
     * @param updateUserDTO
     * @param checkUser
     * @return
     */
    @Override
    public String updateUser(UserDTO updateUserDTO, User checkUser) {
        if (updateUserDTO.getFullName() != null) {
            checkUser.setFullName(updateUserDTO.getFullName());
        }
        if (updateUserDTO.getEmail() != null) {
            checkUser.setEmail(updateUserDTO.getEmail());
        }
        if (updateUserDTO.getPhone() != null) {
            checkUser.setPhone(updateUserDTO.getPhone());
        }
        if (updateUserDTO.getDescription() != null) {
            checkUser.setDescription(updateUserDTO.getDescription());
        }
        if (updateUserDTO.getAge() != null) {
            checkUser.setAge(updateUserDTO.getAge());
        }
        if (updateUserDTO.getBirthday() != null) {
            checkUser.setBirthday(updateUserDTO.getBirthday());
        }
        if (updateUserDTO.getJob() != null) {
            checkUser.setJob(updateUserDTO.getJob());
        }
        try {
            userRepository.save(checkUser);
            return "done";
        } catch (Exception e) {
            return "Lỗi khi cập nhật: " + e.getMessage();
        }
    }

    @Override
    public User getUserByName(String name) {
        User user = userRepository.findByUserName(name);
        return user;
    }

    @Override
    public List<UserDTO> getUserByFullName(String fullName) {
        return userRepository.searchByUserName(fullName).stream().map(user -> {
            return new UserDTO(
                    user.getUserName(),
                    user.getFullName(),
                    null,
                    user.getEmail(),
                    user.getDescription(),
                    user.getPhone(),
                    user.getUserImgUrl(),
                    user.getAge(),
                    user.getBirthday(),
                    user.getIdNumber(),
                    user.getJob(),
                    user.getApartment() != null ? user.getApartment().getApartmentName() : null,
                    user.getRole()
            );
        }).collect(Collectors.toList());
    }

    @Override
    public String deleteUserById(Long id) {
        userRepository.deleteById(id);
        return "done";
    }

    @Override
    public String verifyUser(VerifyUserDTO verifyUserDTO, List<MultipartFile> imageFile) {
        VerificationForm verificationForm = new VerificationForm();
        verificationForm.setFullName(verifyUserDTO.getFullName());
        verificationForm.setEmail(verifyUserDTO.getEmail());
        verificationForm.setContractEndDate(verifyUserDTO.getContractEndDate());
        verificationForm.setContractStartDate(verifyUserDTO.getContractStartDate());
        verificationForm.setPhoneNumber(verifyUserDTO.getPhoneNumber());

        verificationForm = verificationFormRepository.save(verificationForm);

        List<ContractImages> contractImages = new ArrayList<>();

        for (MultipartFile file : imageFile) {
            ContractImages contractImage = new ContractImages();
            contractImage.setImageUrl(imageUploadService.uploadImage(file));
            contractImages.add(contractImage);
        }

        verificationForm.setContractImages(contractImages);

        return "success";
    }

}
