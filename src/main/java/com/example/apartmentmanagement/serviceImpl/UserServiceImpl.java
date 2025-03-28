package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.*;
import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.ContractImages;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.entities.VerificationForm;
import com.example.apartmentmanagement.repository.ApartmentRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.repository.VerificationFormRepository;
import com.example.apartmentmanagement.service.EmailService;
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

    @Autowired
    private EmailService emailService;

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

            User user = userRepository.findByUserName(verificationForm.getUserName());

            dto.setUserRole(user.getRole());

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
    public List<UserRequestDTO> showAllUser() {
        return userRepository.findAll().stream().map(user -> {
            UserRequestDTO dto = new UserRequestDTO();
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
        if (verificationForm == null) {
            throw new RuntimeException("Không tìm thấy form xác minh");
        }

        User user = userRepository.findByUserName(newAccountDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("Chưa có user này trong hệ thống");
        }

        Apartment apartment = apartmentRepository.findApartmentByApartmentName(newAccountDTO.getApartmentName());
        if (apartment == null) {
            throw new RuntimeException("Không tìm thấy căn hộ");
        }

        if (apartment.getHouseholder() == null && "Owner".equals(newAccountDTO.getUserRole())) {
            apartment.setHouseholder(user.getUserName());
        } else if (apartment.getHouseholder() != null && "Owner".equals(newAccountDTO.getUserRole())) {
            throw new RuntimeException("Đã có chủ hộ, không thể thêm chủ sở hữu mới.");
        }

        apartment.setTotalNumber(apartment.getTotalNumber() + 1);
        if (apartment.getTotalNumber() > 0) {
            apartment.setStatus("rented");
        }
        apartmentRepository.save(apartment);
        if (verificationForm.getVerificationFormType() == 1) {
            user.setRole("Rentor");
        } else if (verificationForm.getVerificationFormType() == 2) {
            user.setRole("Owner");
        }

        user.setVerificationForm(verificationForm);

        userRepository.save(user);

        verificationForm.setVerified(true);
        verificationFormRepository.save(verificationForm);

        Long userId = user.getUserId();
        Long apartmentId = apartment.getApartmentId();

        System.out.println("User ID: " + user.getUserId());
        System.out.println("Apartment ID: " + apartmentId);

        userRepository.addUserToApartment(userId, apartmentId);

        emailService.sendVerificationEmail(user.getEmail(), newAccountDTO.getUsername());

        // Chuẩn bị dữ liệu phản hồi
        List<ApartmentResponseDTO> apartmentResponseDTOList = user.getApartments().stream()
                .map(apartment1 -> new ApartmentResponseDTO(
                        apartment1.getApartmentId(),
                        apartment1.getApartmentName(),
                        apartment1.getHouseholder(),
                        apartment1.getTotalNumber(),
                        apartment1.getStatus(),
                        apartment1.getAptImgUrl(),
                        apartment1.getNumberOfBedrooms(),
                        apartment1.getNumberOfBathrooms(),
                        apartment1.getNote(),
                        apartment1.getDirection(),
                        apartment1.getFloor(),
                        apartment1.getArea()
                ))
                .toList();

        return new AddNewResidentResponseDTO(
                user.getUserName(),
                apartmentResponseDTOList,
                user.getFullName(),
                user.getRole(),
                true
        );
    }


    @Override
    public RegisterResponseDTO register(VerifyOTPRequestDTO registerRequestDTO) {
        User user = new User();
        user.setUserName(registerRequestDTO.getUserName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPhone(registerRequestDTO.getPhone());
        user.setPassword(registerRequestDTO.getPassword());
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
                AESUtil.encrypt(verifyRegisterRequestDTO.getPassword()),
                AESUtil.encrypt(verifyRegisterRequestDTO.getRe_password()),
                verifyRegisterRequestDTO.getEmail(),
                verifyRegisterRequestDTO.getPhone()
        );
    }

    @Override
    public UserRequestDTO getUserById(Long id) {
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

        return new UserRequestDTO(
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
    public UserRequestDTO getUserDTOById(Long id) {
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

        return new UserRequestDTO(
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
     * @param updateUserRequestDTO
     * @param checkUser
     * @return
     */
    @Override
    public UserResponseDTO updateUser(UserRequestDTO updateUserRequestDTO, User checkUser) {
        if (updateUserRequestDTO.getFullName() != null) {
            checkUser.setFullName(updateUserRequestDTO.getFullName());
        }
        if (updateUserRequestDTO.getEmail() != null) {
            checkUser.setEmail(updateUserRequestDTO.getEmail());
        }
        if (updateUserRequestDTO.getPhone() != null) {
            checkUser.setPhone(updateUserRequestDTO.getPhone());
        }
        if (updateUserRequestDTO.getDescription() != null) {
            checkUser.setDescription(updateUserRequestDTO.getDescription());
        }
        if (updateUserRequestDTO.getAge() != null) {
            checkUser.setAge(updateUserRequestDTO.getAge());
        }
        if (updateUserRequestDTO.getBirthday() != null) {
            checkUser.setBirthday(updateUserRequestDTO.getBirthday());
        }
        if (updateUserRequestDTO.getJob() != null) {
            checkUser.setJob(updateUserRequestDTO.getJob());
        }
        if (updateUserRequestDTO.getIdNumber() != null) {
            checkUser.setIdNumber(updateUserRequestDTO.getIdNumber());
        }
        if (updateUserRequestDTO.getUserImgUrl() != null) {
            checkUser.setUserImgUrl(updateUserRequestDTO.getUserImgUrl());
        }

        User updatedUser = userRepository.save(checkUser);

        try {
            List<ApartmentResponseInUserDTO> apartmentDTOList = Optional.ofNullable(checkUser.getApartments())
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

            return new UserResponseDTO(
                    updatedUser.getUserId(),
                    updatedUser.getUserName(),
                    updatedUser.getFullName(),
                    updatedUser.getEmail(),
                    updatedUser.getDescription(),
                    updatedUser.getPhone(),
                    updatedUser.getUserImgUrl(),
                    updatedUser.getAge(),
                    updatedUser.getBirthday(),
                    updatedUser.getIdNumber(),
                    updatedUser.getJob(),
                    apartmentDTOList,
                    updatedUser.getRole()
            );

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật người dùng: " + e.getMessage());
        }
    }

    @Override
    public User getUserByName(String name) {
        User user = userRepository.findByUserName(name);
        return user;
    }

    @Override
    public List<UserRequestDTO> getUserByFullName(String fullName) {
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

            return new UserRequestDTO(
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
                user.getRole(),
                verificationForm.getVerificationFormId(),
                verificationForm.getVerificationFormType(),
                verificationForm.getApartmentName(),
                verificationForm.getUserName(),
                verificationForm.isVerified()
        );
    }
}
