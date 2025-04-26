package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.VerifyUserRequestDTO;
import com.example.apartmentmanagement.dto.VerifyUserResponseDTO;
import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.ContractImages;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.entities.VerificationForm;
import com.example.apartmentmanagement.repository.ApartmentRepository;
import com.example.apartmentmanagement.repository.ContractImagesRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.repository.VerificationFormRepository;
import com.example.apartmentmanagement.service.NotificationService;
import com.example.apartmentmanagement.service.UserService;
import com.example.apartmentmanagement.service.VerificationFormService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class VerificationFormServiceImpl implements VerificationFormService {

    @Autowired
    private VerificationFormRepository verificationFormRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private NotificationService notificationService;

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

        User user = userService.getUserByEmailOrUserName(verificationForm.getEmail());

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
