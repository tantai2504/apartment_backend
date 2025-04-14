package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.*;
import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.ContractImages;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.entities.VerificationForm;
import com.example.apartmentmanagement.repository.*;
import com.example.apartmentmanagement.service.EmailService;
import com.example.apartmentmanagement.service.NotificationService;
import com.example.apartmentmanagement.service.UserService;
import com.example.apartmentmanagement.util.AESUtil;
import jakarta.transaction.Transactional;
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

    @Autowired
    private ContractImagesRepository contractImagesRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void removeUserVerificationForm(Long verificationFormId) {
        VerificationForm verificationForm = verificationFormRepository.findById(verificationFormId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy form xác minh"));

        User user = userRepository.findByUserName(verificationForm.getUserName());

        if (user != null) {
            user.setVerificationForm(null);
            userRepository.save(user);
        }

        verificationFormRepository.delete(verificationForm);
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
    public List<VerifyUserResponseDTO> showAllContract(String apartmentName) {
        List<VerificationForm> forms = verificationFormRepository.findByApartmentNameIgnoreCaseAndVerifiedAndVerificationFormType(apartmentName, true, 1);

        return forms.stream().map(verificationForm -> {
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
    public VerifyUserResponseDTO findVerificationByUserName(String userName) {
        VerificationForm verificationForm = verificationFormRepository.findVerificationFormByUserNameContainingIgnoreCase(userName);
        if (verificationForm == null) {
            throw new RuntimeException("Không tìm thấy hợp đồng này");
        }
        List<ContractImages> contractImages = verificationForm.getContractImages();
        return new VerifyUserResponseDTO(
                verificationForm.getVerificationFormId(),
                verificationForm.getVerificationFormName(),
                verificationForm.getFullName(),
                verificationForm.getEmail(),
                verificationForm.getPhoneNumber(),
                verificationForm.getContractStartDate(),
                verificationForm.getContractEndDate(),
                contractImages.stream().map(ContractImages::getImageUrl).toList(),
                verificationForm.getUser().getRole(),
                verificationForm.getVerificationFormId(),
                verificationForm.getVerificationFormType(),
                verificationForm.getApartmentName(),
                verificationForm.getUserName(),
                verificationForm.isVerified()
        );
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

        if (verificationForm.getVerificationFormType() == 2) {
            if (apartment.getHouseholder() == null) {
                user.setRole("Owner");
                apartment.setHouseholder(user.getUserName());
                apartment.setStatus("sold");
            } else {
                throw new RuntimeException("Căn hộ này đã có chủ sở hữu");
            }
        }

        if (verificationForm.getVerificationFormType() == 1) {
            user.setRole("Rentor");
            apartment.setStatus("rented");
            apartment.setTotalNumber(apartment.getTotalNumber() + 1);
        }

        apartmentRepository.save(apartment);

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

        VerificationForm verificationForm = verificationFormRepository.findById(checkUser.getUserId()).orElse(null);

        if (updateUserRequestDTO.getFullName() != null) {
            checkUser.setFullName(updateUserRequestDTO.getFullName());
            if (verificationForm != null) {
                verificationForm.setFullName(updateUserRequestDTO.getFullName());
            }
        }
        if (updateUserRequestDTO.getEmail() != null) {
            checkUser.setEmail(updateUserRequestDTO.getEmail());
            if (verificationForm != null) {
                verificationForm.setEmail(updateUserRequestDTO.getEmail());
            }
        }
        if (updateUserRequestDTO.getPhone() != null) {
            checkUser.setPhone(updateUserRequestDTO.getPhone());
            if (verificationForm != null) {
                verificationForm.setPhoneNumber(updateUserRequestDTO.getPhone());
            }
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
        verificationFormRepository.save(verificationForm);

        try {
            List<ApartmentResponseInUserDTO> apartmentDTOList = Optional.ofNullable(checkUser.getApartments())
                    .map(apartments -> apartments.stream()
                            .map(apartment -> new ApartmentResponseInUserDTO(
                                    apartment.getApartmentId(),
                                    apartment.getApartmentName(),
                                    apartment.getHouseholder(),
                                    apartment.getTotalNumber(),
                                    apartment.getStatus(),
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

    @Transactional
    @Override
    public void removeOwner(Long userId, Long apartmentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new RuntimeException("Apartment not found"));
        VerificationForm verificationForm = user.getVerificationForm();

        boolean hasOtherRentor = apartment.getUsers().stream()
                .anyMatch(u -> !u.getUserId().equals(userId) && "Rentor".equals(u.getRole()));

        if (hasOtherRentor) {
            throw new RuntimeException("Vẫn còn rentor tồn tại trong căn hộ");
        }

        apartment.setTotalNumber(apartment.getTotalNumber() - 1);
        apartment.setStatus("unrented");
        apartment.setHouseholder(null);
        user.setRole("User");

        verificationForm.setVerified(false);
        verificationForm.setExpired(true);
        verificationFormRepository.save(verificationForm);

        user.getApartments().remove(apartment);
        apartment.getUsers().remove(user);

        userRepository.save(user);
        apartmentRepository.save(apartment);
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

    @Transactional
    @Override
    public void removeRentorById(Long apartmentId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new RuntimeException("Apartment not found"));

        VerificationForm verificationForm = user.getVerificationForm();

        if (user.getRole().equals("Owner")) {
            throw new RuntimeException("Không thể xoá owner ra khỏi căn hộ này");
        }

        apartment.setTotalNumber(apartment.getTotalNumber() - 1);
        user.setRole("Rentor");

        user.getApartments().remove(apartment);
        apartment.getUsers().remove(user);

        verificationForm.setVerified(false);
        verificationForm.setExpired(true);
        verificationFormRepository.save(verificationForm);

        userRepository.save(user);
        apartmentRepository.save(apartment);
    }

    @Override
    public void setCurrentStatusForApartment(Long apartmentId) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new RuntimeException("Apartment not found"));

        List<User> users = apartment.getUsers();

        boolean hasRentor = users.stream()
                .anyMatch(user -> "Rentor".equalsIgnoreCase(user.getRole()));

        if (!hasRentor) {
            apartment.setStatus("sold");
            apartmentRepository.save(apartment);
        }
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
                verificationForm.getVerificationFormId(),
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


//    @Override
//    public VerifyUserResponseDTO updateVerifyUser(Long verificationUserId, VerifyUserRequestDTO verifyUserDTO, List<MultipartFile> imageFiles) {
//        VerificationForm verificationForm = verificationFormRepository.findById(verificationUserId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn xác thực với ID: " + verificationUserId));
//
//        User user = getUserByEmailOrUserName(verifyUserDTO.getEmail());
//
//        verificationForm.setContractStartDate(verifyUserDTO.getContractStartDate());
//        verificationForm.setContractEndDate(verifyUserDTO.getContractEndDate());
//
//        // Xoá ảnh cũ nếu có
//        List<ContractImages> oldImages = verificationForm.getContractImages();
//        if (oldImages != null) {
//            contractImagesRepository.deleteAll(oldImages);
//        }
//
//        // Upload ảnh mới
//        List<ContractImages> newContractImages = new ArrayList<>();
//        for (MultipartFile file : imageFiles) {
//            ContractImages contractImage = new ContractImages();
//            contractImage.setImageUrl(imageUploadService.uploadImage(file));
//            contractImage.setVerificationForm(verificationForm);
//            newContractImages.add(contractImage);
//        }
//
//        verificationForm.setContractImages(newContractImages);
//        verificationForm = verificationFormRepository.save(verificationForm);
//
//        return new VerifyUserResponseDTO(
//                verificationForm.getVerificationFormId(),
//                verificationForm.getVerificationFormName(),
//                verificationForm.getFullName(),
//                verificationForm.getEmail(),
//                verificationForm.getPhoneNumber(),
//                verificationForm.getContractStartDate(),
//                verificationForm.getContractEndDate(),
//                newContractImages.stream().map(ContractImages::getImageUrl).toList(),
//                user.getRole(),
//                verificationForm.getVerificationFormId(),
//                verificationForm.getVerificationFormType(),
//                verificationForm.getApartmentName(),
//                verificationForm.getUserName(),
//                verificationForm.isVerified()
//        );
//    }

    @Override
    public VerifyUserResponseDTO updateVerifyUser(Long verificationUserId, VerifyUserRequestDTO verifyUserDTO, List<MultipartFile> imageFiles) {
        VerificationForm verificationForm = verificationFormRepository.findById(verificationUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn xác thực với ID: " + verificationUserId));

        verificationForm.setContractStartDate(verifyUserDTO.getContractStartDate());
        verificationForm.setContractEndDate(verifyUserDTO.getContractEndDate());

        if (verificationForm.getContractImages() == null) {
            verificationForm.setContractImages(new ArrayList<>());
        } else {
            verificationForm.getContractImages().clear();
        }

        for (MultipartFile file : imageFiles) {
            ContractImages contractImage = new ContractImages();
            contractImage.setImageUrl(imageUploadService.uploadImage(file));
            contractImage.setVerificationForm(verificationForm); // Quan hệ ngược
            verificationForm.getContractImages().add(contractImage);
        }

        verificationForm = verificationFormRepository.save(verificationForm);

        User user = getUserByEmailOrUserName(verificationForm.getEmail());

        return new VerifyUserResponseDTO(
                verificationForm.getVerificationFormId(),
                verificationForm.getVerificationFormName(),
                verificationForm.getFullName(),
                verificationForm.getEmail(),
                verificationForm.getPhoneNumber(),
                verificationForm.getContractStartDate(),
                verificationForm.getContractEndDate(),
                verificationForm.getContractImages().stream().map(ContractImages::getImageUrl).toList(),
                user.getRole(),
                verificationForm.getVerificationFormId(),
                verificationForm.getVerificationFormType(),
                verificationForm.getApartmentName(),
                verificationForm.getUserName(),
                verificationForm.isVerified()
        );
    }

    @Override
    public Map<String, Object> show_user_and_role() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> apartmentList = new ArrayList<>();

        List<Apartment> apartments = apartmentRepository.findAll();
        for (Apartment apartment : apartments) {
            Map<String, Object> apartmentInfo = new HashMap<>();
            List<User> users = apartment.getUsers();

            apartmentInfo.put("apartment", apartment.getApartmentName());
            apartmentInfo.put("users", users.stream().map(User::getUserName).collect(Collectors.toList()));
            apartmentInfo.put("roles", users.stream().map(User::getRole).collect(Collectors.toList()));

            apartmentList.add(apartmentInfo);
        }

        response.put("apartments", apartmentList);
        return response;
    }

    @Override
    @Transactional
    public void terminateContract(Long userId, Long apartmentId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy căn hộ"));

        // Kiểm tra xem người dùng có phải là người thuê căn hộ này không
        if (!user.getApartments().contains(apartment)) {
            throw new RuntimeException("Người dùng không thuê căn hộ này");
        }

        if (!"Rentor".equals(user.getRole())) {
            throw new RuntimeException("Người dùng này không phải là người thuê");
        }

        // Tìm form xác minh/hợp đồng liên quan
        VerificationForm contract = null;
        if (user.getVerificationForm() != null &&
                user.getVerificationForm().getApartmentName().equals(apartment.getApartmentName())) {
            contract = user.getVerificationForm();
        } else {
            // Tìm trong danh sách các hợp đồng đã xác minh
            List<VerificationForm> verificationForms = verificationFormRepository
                    .findByApartmentNameIgnoreCaseAndVerified(apartment.getApartmentName(), true);

            for (VerificationForm form : verificationForms) {
                if (form.getUserName().equals(user.getUserName())) {
                    contract = form;
                    break;
                }
            }
        }

        if (contract == null) {
            throw new RuntimeException("Không tìm thấy hợp đồng liên quan");
        }

        // Loại bỏ người thuê khỏi căn hộ
        user.getApartments().remove(apartment);
        apartment.getUsers().remove(user);

        // Cập nhật số lượng người trong căn hộ
        if (apartment.getTotalNumber() > 0) {
            apartment.setTotalNumber(apartment.getTotalNumber() - 1);
        }

        // Nếu không còn ai thuê, đặt trạng thái là "sold" (chỉ còn chủ sở hữu)
        if (apartment.getUsers().stream().noneMatch(u -> "Rentor".equals(u.getRole()))) {
            apartment.setStatus("sold");
        }

        // Kiểm tra xem user còn thuê căn hộ nào khác không
        // Nếu không còn, chuyển vai trò từ Rentor thành User
        boolean stillRenting = false;
        for (Apartment userApartment : user.getApartments()) {
            if (!userApartment.equals(apartment)) {
                stillRenting = true;
                break;
            }
        }

        if (!stillRenting) {
            user.setRole("User");
        }

        // Đánh dấu hợp đồng đã kết thúc
        contract.setExpired(true);
        contract.setVerified(false);

        // Lưu các thay đổi
        apartmentRepository.save(apartment);
        userRepository.save(user);
        verificationFormRepository.save(contract);

        // Gửi thông báo cho người thuê
        notificationService.createAndBroadcastNotification(
                String.format("Hợp đồng thuê căn hộ %s của bạn đã bị chấm dứt với lý do: %s",
                        apartment.getApartmentName(), reason),
                "Thông báo chấm dứt hợp đồng",
                user.getUserId()
        );

        // Gửi thông báo cho chủ nhà nếu có
        if (apartment.getHouseholder() != null) {
            User owner = userRepository.findByUserName(apartment.getHouseholder());
            if (owner != null && !owner.getUserId().equals(user.getUserId())) {
                notificationService.createAndBroadcastNotification(
                        String.format("Hợp đồng thuê căn hộ %s của người thuê %s đã bị chấm dứt.",
                                apartment.getApartmentName(), user.getFullName()),
                        "Thông báo chấm dứt hợp đồng",
                        owner.getUserId()
                );
            }
        }

        // Nếu vai trò đã chuyển từ Rentor sang User
        if (!stillRenting) {
            notificationService.createAndBroadcastNotification(
                    "Bạn không còn thuê căn hộ nào. Vai trò của bạn đã được chuyển thành User.",
                    "Thông báo cập nhật vai trò",
                    user.getUserId()
            );
        }
    }
}
