package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.*;
import com.example.apartmentmanagement.entities.*;
import com.example.apartmentmanagement.repository.*;
import com.example.apartmentmanagement.service.EmailService;
import com.example.apartmentmanagement.service.NotificationService;
import com.example.apartmentmanagement.service.PostService;
import com.example.apartmentmanagement.service.UserService;
import com.example.apartmentmanagement.util.AESUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private VerificationFormRepository verificationFormRepository;

    @Autowired
    private EmailService emailService;

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

        Apartment apartment = apartmentRepository.findApartmentByApartmentName(newAccountDTO.getApartmentName());
        System.out.println(apartment);

        if (apartment == null) {
            throw new RuntimeException("Không tìm thấy căn hộ");
        }

        String apartmentName = apartment.getApartmentName();
        Post post = postRepository.findByApartment_ApartmentName(apartmentName);
        if (post == null) {
            throw new RuntimeException("Không có bài viết nào được đăng tải cho căn hộ này");
        } else {
            postService.deletePost(post.getPostId());
        }

        if (verificationForm == null) {
            throw new RuntimeException("Không tìm thấy form xác minh");
        }

        User user = userRepository.findByUserName(newAccountDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("Chưa có user này trong hệ thống");
        }

        if (!user.getRole().equals("User")) {
            throw new RuntimeException("Người dùng hiện đang ở trong căn hộ khác, không thể thêm vào căn hộ này");
        }


        if (verificationForm.getVerificationFormType() == 2) {
            // Chủ sở hữu
            if (apartment.getHouseholder() == null) {
                user.setRole("Owner");
                apartment.setHouseholder(user.getUserName());
                apartment.setStatus("sold");
            } else {
                throw new RuntimeException("Căn hộ này đã có chủ sở hữu");
            }
        }

        if (verificationForm.getVerificationFormType() == 1) {
            if (apartment.getHouseholder() == null) {
                throw new RuntimeException("Căn hộ này chưa có chủ hộ, không thể thêm người thuê này vào");
            }
            // Người thuê
//            if(!user.getRole().equalsIgnoreCase("Owner")){
//                user.setRole("Rentor");
//            }
            if(user.getRole().equalsIgnoreCase("User")) {
                user.setRole("Rentor");
            }
            user.setRentor(true);
            List<User> lUser = apartment.getUsers();
            lUser.add(user);
            apartment.setUsers(lUser);
            apartment.setStatus("rented");
            apartment.setTotalNumber(apartment.getTotalNumber() + 1);
        }

        Long userId = user.getUserId();
        Long apartmentId = apartment.getApartmentId();

        System.out.println("User ID: " + user.getUserId());
        System.out.println("Apartment ID: " + apartmentId);

        if (verificationForm.getVerificationFormType() == 1) {
            userRepository.addUserToApartment(userId, apartmentId);
        }

        emailService.sendVerificationEmail(user.getEmail(), newAccountDTO.getUsername());

        notificationService.createAndBroadcastNotification(
                "Tài khoản của bạn đã được duyệt thành công!",
                "Thông báo duyệt tài khoản",
                userId
        );

        apartmentRepository.save(apartment);

        user.setVerificationForm(verificationForm);
        userRepository.save(user);

        verificationForm.setVerified(true);
        verificationFormRepository.save(verificationForm);

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

//    @Override
//    public AddNewResidentResponseDTO addUser(VerifyUserResponseDTO newAccountDTO) {
//        VerificationForm verificationForm = verificationFormRepository.findVerificationFormByUserNameContainingIgnoreCase(newAccountDTO.getUsername());
//        if (verificationForm == null) {
//            throw new RuntimeException("Không tìm thấy form xác minh");
//        }
//
//        User user = userRepository.findByUserName(newAccountDTO.getUsername());
//        if (user == null) {
//            throw new RuntimeException("Chưa có user này trong hệ thống");
//        }
//
//        if (!user.getRole().equals("User")) {
//            throw new RuntimeException("Người dùng hiện đang ở trong căn hộ khác, không thể thêm vào căn hộ này");
//        }
//
//        Apartment apartment = apartmentRepository.findApartmentByApartmentName(newAccountDTO.getApartmentName());
//        if (apartment == null) {
//            throw new RuntimeException("Không tìm thấy căn hộ");
//        }
//
//        if (verificationForm.getVerificationFormType() == 2) {
//            // Chủ sở hữu
//            if (apartment.getHouseholder() == null) {
//                user.setRole("Owner");
//                apartment.setHouseholder(user.getUserName());
//                apartment.setStatus("sold");
//            } else {
//                throw new RuntimeException("Căn hộ này đã có chủ sở hữu");
//            }
//        }
//
//        if (verificationForm.getVerificationFormType() == 1) {
//            if (apartment.getHouseholder() == null) {
//                throw new RuntimeException("Căn hộ này chưa có chủ hộ, không thể thêm người thuê này vào");
//            }
//            // Người thuê
////            if(!user.getRole().equalsIgnoreCase("Owner")){
////                user.setRole("Rentor");
////            }
//            if(user.getRole().equalsIgnoreCase("User")) {
//                user.setRole("Rentor");
//            }
//            user.setRentor(true);
//            List<User> lUser = apartment.getUsers();
//            lUser.add(user);
//            apartment.setUsers(lUser);
//            apartment.setStatus("rented");
//            apartment.setTotalNumber(apartment.getTotalNumber() + 1);
//        }
//
//        apartmentRepository.save(apartment);
//
//        user.setVerificationForm(verificationForm);
//        userRepository.save(user);
//
//        verificationForm.setVerified(true);
//        verificationFormRepository.save(verificationForm);
//
//        Long userId = user.getUserId();
//        Long apartmentId = apartment.getApartmentId();
//
//        System.out.println("User ID: " + user.getUserId());
//        System.out.println("Apartment ID: " + apartmentId);
//
//        if (verificationForm.getVerificationFormType() == 1) {
//            userRepository.addUserToApartment(userId, apartmentId);
//        }
//
//        emailService.sendVerificationEmail(user.getEmail(), newAccountDTO.getUsername());
//
//        notificationService.createAndBroadcastNotification(
//                "Tài khoản của bạn đã được duyệt thành công!",
//                "Thông báo duyệt tài khoản",
//                userId
//        );
//
//        String apartmentName = apartment.getApartmentName();
//        Post post = postRepository.findByApartment_ApartmentName(apartmentName);
//        postService.deletePost(post.getPostId());
//
//        List<ApartmentResponseDTO> apartmentResponseDTOList = user.getApartments().stream()
//                .map(apartment1 -> new ApartmentResponseDTO(
//                        apartment1.getApartmentId(),
//                        apartment1.getApartmentName(),
//                        apartment1.getHouseholder(),
//                        apartment1.getTotalNumber(),
//                        apartment1.getStatus(),
//                        apartment1.getNumberOfBedrooms(),
//                        apartment1.getNumberOfBathrooms(),
//                        apartment1.getNote(),
//                        apartment1.getDirection(),
//                        apartment1.getFloor(),
//                        apartment1.getArea()
//                ))
//                .toList();
//
//        return new AddNewResidentResponseDTO(
//                user.getUserName(),
//                apartmentResponseDTOList,
//                user.getFullName(),
//                user.getRole(),
//                true
//        );
//    }

    @Override
    public RegisterResponseDTO register(VerifyOTPRequestDTO registerRequestDTO) {
        User user = new User();
        user.setUserName(registerRequestDTO.getUserName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPhone(registerRequestDTO.getPhone());
        user.setPassword(registerRequestDTO.getPassword());
        user.setBirthday(LocalDate.of(1950, 1, 1));
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
     * @return
     */
    @Override
    public UpdateUserResponseDTO updateUser(UpdateUserRequestDTO updateUserRequestDTO) {
        User checkUser = userRepository.findById(updateUserRequestDTO.getUserId()).get();
        if (updateUserRequestDTO.getFullName() != null) {
            checkUser.setFullName(updateUserRequestDTO.getFullName());
        }

        if (updateUserRequestDTO.getPhone() != null) {
            checkUser.setPhone(updateUserRequestDTO.getPhone());
        }
        if (updateUserRequestDTO.getDescription() != null) {
            checkUser.setDescription(updateUserRequestDTO.getDescription());
        }

        if (updateUserRequestDTO.getBirthday() != null) {
            checkUser.setBirthday(updateUserRequestDTO.getBirthday());
            LocalDate birthday = updateUserRequestDTO.getBirthday();
            LocalDate today = LocalDate.now();
            int age = Period.between(birthday, today).getYears();
            String ageStr = String.valueOf(age);
            checkUser.setAge(ageStr);
        }

        if (updateUserRequestDTO.getJob() != null) {
            checkUser.setJob(updateUserRequestDTO.getJob());
        }

        if (updateUserRequestDTO.getUserImgUrl() != null) {
            checkUser.setUserImgUrl(updateUserRequestDTO.getUserImgUrl());
        }

        User updatedUser = userRepository.save(checkUser);

        try {
            return new UpdateUserResponseDTO(
                    updatedUser.getUserId(),
                    updatedUser.getUserName(),
                    updatedUser.getPassword(),
                    updatedUser.getFullName(),
                    updatedUser.getEmail(),
                    updatedUser.getPhone(),
                    updatedUser.getRole(),
                    updatedUser.getDescription(),
                    updatedUser.getUserImgUrl(),
                    updatedUser.getAge(),
                    updatedUser.getBirthday(),
                    updatedUser.getIdNumber(),
                    updatedUser.getJob(),
                    updatedUser.isRentor(),
                    updatedUser.getAccountBalance()
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


}
