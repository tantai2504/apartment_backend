package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.*;
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

import java.util.*;
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
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public List<VerifyUserResponseDTO> showAllVerifyUser() {
        return verificationFormRepository.findAll().stream().map(verificationForm -> {
            VerifyUserResponseDTO dto = new VerifyUserResponseDTO();
            dto.setVerificationFormId(verificationForm.getVerificationFormId());
            dto.setVerificationFormName(verificationForm.getVerificationFormName());
            dto.setVerificationFormType(verificationForm.getVerificationFormType());
            dto.setApartmentName(verificationForm.getApartmentName());
            dto.setFullName(verificationForm.getFullName());
            dto.setUsername(verificationForm.getUserName());
            dto.setEmail(verificationForm.getEmail());
            dto.setPhoneNumber(verificationForm.getPhoneNumber());
            dto.setContractStartDate(verificationForm.getContractStartDate());
            dto.setContractEndDate(verificationForm.getContractEndDate());
            dto.setVerified(verificationForm.isVerified());

            // Chuyển đổi danh sách ảnh
            dto.setImageFiles(
                    verificationForm.getContractImages().stream()
                            .map(ContractImages::getImageUrl)
                            .toList()
            );

            return dto;
        }).toList();
    }


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

            // Dùng ApartmentResponseInUserDTO thay vì ApartmentDTO
            List<ApartmentResponseInUserDTO> apartmentDTOList = Optional.ofNullable(user.getApartments())
                    .map(apartments -> apartments.stream()
                            .map(apartment -> new ApartmentResponseInUserDTO(
                                    apartment.getApartmentId(),
                                    apartment.getApartmentName(),
                                    apartment.getHouseholder(),
                                    apartment.getTotalNumber(),
                                    apartment.getStatus(),
                                    apartment.getAptImgUrl(),
                                    apartment.getNumberOfBedrooms(),
                                    apartment.getNumberOfBathrooms(),
                                    apartment.getNote()
                            ))
                            .toList())
                    .orElse(List.of());

            dto.setApartment(apartmentDTOList);
            return dto;
        }).toList();
    }


    @Override
    public AddNewResidentResponseDTO addUser(VerifyUserResponseDTO newAccountDTO) {
        VerificationForm verificationForm = verificationFormRepository.findVerificationFormByUserNameContainingIgnoreCase(newAccountDTO.getUsername());
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!user.getUserName().equals(newAccountDTO.getUsername())) {
                throw new RuntimeException("Chưa có user này trong hệ thống");
            }
        }
        User user = userRepository.findByUserName(newAccountDTO.getUsername());

        List<Apartment> apartments = new ArrayList<>();
        Apartment apartment = apartmentRepository.findApartmentByApartmentName(newAccountDTO.getApartmentName());
        if (apartment.getHouseholder() == null && newAccountDTO.getUserRole().equals("Owner")) {
            apartment.setHouseholder(newAccountDTO.getUserRole());
        } else if (apartment.getHouseholder() != null && newAccountDTO.getUserRole().equals("Owner")) {
            throw new RuntimeException("Đã có người đứng tên chủ căn hộ, không thể thêm chủ sở hữu mới.");
        }
        apartments.add(apartment);

        apartment.setTotalNumber(apartment.getTotalNumber() + 1);
        if (apartment.getTotalNumber() > 0) {
            apartment.setStatus("rented");
        }

        user.setRole(newAccountDTO.getUserRole());
        user.setVerificationForm(verificationForm);
        user.setApartments(apartments);

        verificationForm.setVerified(true);
        verificationFormRepository.save(verificationForm);

        userRepository.save(user);


        List<ApartmentDTO> apartmentDTOList = user.getApartments().stream()
                .map(apartment1 -> new ApartmentDTO(
                        apartment.getApartmentId(),
                        apartment.getApartmentName(),
                        apartment.getHouseholder(),
                        apartment.getTotalNumber(),
                        apartment.getStatus(),
                        apartment.getAptImgUrl(),
                        apartment.getNumberOfBedrooms(),
                        apartment.getNumberOfBathrooms(),
                        apartment.getNote()
                ))
                .toList();

        AddNewResidentResponseDTO responseDTO = new AddNewResidentResponseDTO(
                user.getUserName(),
                apartmentDTOList,
                user.getFullName(),
                user.getRole(),
                true
        );

        return responseDTO;
    }

    @Override
    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        User user = new User();
        user.setUserName(registerRequestDTO.getUserName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPhone(registerRequestDTO.getPhone());
        user.setPassword(AESUtil.encrypt(registerRequestDTO.getPassword()));
        user.setRole("User");
        userRepository.save(user);
        RegisterResponseDTO responseDTO = new RegisterResponseDTO(
                user.getUserId(),
                user.getUserName(),
                user.getPassword(),
                user.getEmail(),
                user.getPhone()
        );
        return responseDTO;
    }

    @Override
    public VerifyRegisterRequestDTO verifyRegister(RegisterRequestDTO verifyRegisterRequestDTO) {
        List<User> users = userRepository.findAll();
        for (User u : users) {
            if (u.getUserName().equals(verifyRegisterRequestDTO.getUserName())) {
                throw new RuntimeException("Đã có username này");
            }
            if (u.getEmail().equals(verifyRegisterRequestDTO.getEmail())) {
                throw new RuntimeException("Đã có email này");
            }
        }
        if (!verifyRegisterRequestDTO.getPassword().equals(verifyRegisterRequestDTO.getRe_password())) {
            throw new RuntimeException("Mật khẩu không trùng khớp");
        }

        return new VerifyRegisterRequestDTO(
                verifyRegisterRequestDTO.getUserName(),
                verifyRegisterRequestDTO.getEmail(),
                verifyRegisterRequestDTO.getPassword(),
                verifyRegisterRequestDTO.getRe_password(),
                verifyRegisterRequestDTO.getPhone()
        );
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }

        List<ApartmentResponseInUserDTO> apartmentDTOList = Optional.ofNullable(user.getApartments())
                .map(apartments -> apartments.stream()
                        .map(apartment -> new ApartmentResponseInUserDTO(
                                apartment.getApartmentId(),
                                apartment.getApartmentName(),
                                apartment.getHouseholder(),
                                apartment.getTotalNumber(),
                                apartment.getStatus(),
                                apartment.getAptImgUrl(),
                                apartment.getNumberOfBedrooms(),
                                apartment.getNumberOfBathrooms(),
                                apartment.getNote()
                        ))
                        .toList())
                .orElse(List.of());

        return new UserDTO(
                user.getUserId(),
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
                apartmentDTOList,
                user.getRole()
        );
    }


    @Override
    public UserDTO getUserDTOById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null; // Tránh lỗi NullPointerException
        }

        List<ApartmentResponseInUserDTO> apartmentDTOList = user.getApartments().stream()
                .map(apartment -> new ApartmentResponseInUserDTO(
                        apartment.getApartmentId(),
                        apartment.getApartmentName(),
                        apartment.getHouseholder(),
                        apartment.getTotalNumber(),
                        apartment.getStatus(),
                        apartment.getAptImgUrl(),
                        apartment.getNumberOfBedrooms(),
                        apartment.getNumberOfBathrooms(),
                        apartment.getNote()
                ))
                .toList();

        return new UserDTO(
                user.getUserId(),
                user.getUserName(),
                user.getFullName(),
                AESUtil.decrypt(user.getPassword()),
                user.getEmail(),
                user.getDescription(),
                user.getPhone(),
                user.getUserImgUrl(),
                user.getAge(),
                user.getBirthday(),
                user.getIdNumber(),
                user.getJob(),
                apartmentDTOList,
                user.getRole()
        );
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
        if (updateUserDTO.getIdNumber() != null) {
            checkUser.setIdNumber(updateUserDTO.getIdNumber());
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
            List<ApartmentResponseInUserDTO> apartmentDTOList = Optional.ofNullable(user.getApartments())
                    .map(apartments -> apartments.stream()
                            .map(apartment -> new ApartmentResponseInUserDTO(
                                    apartment.getApartmentId(),
                                    apartment.getApartmentName(),
                                    apartment.getHouseholder(),
                                    apartment.getTotalNumber(),
                                    apartment.getStatus(),
                                    apartment.getAptImgUrl(),
                                    apartment.getNumberOfBedrooms(),
                                    apartment.getNumberOfBathrooms(),
                                    apartment.getNote()
                            ))
                            .toList())
                    .orElse(List.of());

            return new UserDTO(
                    user.getUserId(),
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
                    apartmentDTOList,
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
    public User getUserByEmailOrUserName(String emailOrUserName) {
        User user = userRepository.findByUserNameOrEmail(emailOrUserName);
        return user;
    }

    @Override
    public VerifyUserResponseDTO verifyUser(VerifyUserRequestDTO verifyUserDTO, List<MultipartFile> imageFiles) {

        User user = getUserByEmailOrUserName(verifyUserDTO.getEmail());

        List<VerificationForm> verificationFormList = verificationFormRepository.findAll();

        String setNewRole = "";
        if (verifyUserDTO.getVerificationFormType()==1) {
            setNewRole = "Rentor";
        } else if (verifyUserDTO.getVerificationFormType()==2) {
            setNewRole = "Owner";
        }

        VerificationForm verificationForm = new VerificationForm();
        verificationForm.setVerificationFormName(verifyUserDTO.getVerificationFormName());
        verificationForm.setVerificationFormType(verifyUserDTO.getVerificationFormType());
        verificationForm.setFullName(user.getFullName());

        for (VerificationForm verificationForm1 : verificationFormList) {
            if (verificationForm1.getEmail().equals(verifyUserDTO.getEmail()) && verificationForm1.getApartmentName().equals(verifyUserDTO.getApartmentName())) {
                throw new RuntimeException("Đã gửi request của user này");
            }
        }
        verificationForm.setEmail(verifyUserDTO.getEmail());
        verificationForm.setApartmentName(verifyUserDTO.getApartmentName());
        verificationForm.setPhoneNumber(user.getPhone());
        verificationForm.setContractStartDate(verifyUserDTO.getContractStartDate());
        verificationForm.setContractEndDate(verifyUserDTO.getContractEndDate());
        verificationForm.setUserName(user.getUserName());
        verificationForm.setFullName(user.getFullName());
        verificationForm.setVerified(false);

        verificationForm = verificationFormRepository.save(verificationForm);

        List<ContractImages> contractImages = new ArrayList<>();
        for (MultipartFile file : imageFiles) {
            ContractImages contractImage = new ContractImages();
            contractImage.setImageUrl(imageUploadService.uploadImage(file));
            contractImage.setVerificationForm(verificationForm);
            contractImages.add(contractImage);
        }

        verificationForm.setContractImages(contractImages);
        verificationForm = verificationFormRepository.save(verificationForm);

        return new VerifyUserResponseDTO(
                verificationForm.getVerificationFormName(),
                verificationForm.getFullName(),
                verificationForm.getEmail(),
                verificationForm.getPhoneNumber(),
                verificationForm.getContractStartDate(),
                verificationForm.getContractEndDate(),
                contractImages.stream().map(ContractImages::getImageUrl).toList(),
                setNewRole,
                verificationForm.getVerificationFormId(),
                verificationForm.getVerificationFormType(),
                verificationForm.getApartmentName(),
                verificationForm.getUserName(),
                verificationForm.isVerified()
        );
    }
}
