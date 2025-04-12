package com.example.apartmentmanagement.scheduler;

import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.entities.VerificationForm;
import com.example.apartmentmanagement.repository.ApartmentRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.repository.VerificationFormRepository;
import com.example.apartmentmanagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ContractExpirationScheduler {

    @Autowired
    private VerificationFormRepository verificationFormRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private NotificationService notificationService;

    /**
     * Kiểm tra và gửi thông báo cho hợp đồng sắp hết hạn (trong vòng 7 ngày)
     * Chạy hàng ngày lúc 00:00
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkUpcomingExpirations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekFromNow = now.plusWeeks(1);

        List<VerificationForm> expiringContracts = verificationFormRepository.findByContractEndDateBetweenAndVerifiedIsTrue(now, oneWeekFromNow);

        for (VerificationForm contract : expiringContracts) {
            // Gửi thông báo cho người dùng có hợp đồng sắp hết hạn
            if (contract.getUser() != null && contract.getUser().getUserId() != null) {
                notificationService.createAndBroadcastNotification(
                        String.format("Hợp đồng của bạn tại căn hộ %s sẽ hết hạn vào ngày %s.",
                                contract.getApartmentName(),
                                contract.getContractEndDate().toLocalDate()
                        ),
                        "Thông báo hết hợp đồng",
                        contract.getUser().getUserId()
                );
            }
        }
    }

    /**
     * Xử lý hợp đồng đã hết hạn
     * Chạy hàng ngày lúc 01:00
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void processExpiredContracts() {
        LocalDateTime now = LocalDateTime.now();

        // Tìm các hợp đồng thuê đã hết hạn và chưa được xử lý (type = 1)
        List<VerificationForm> expiredContracts = verificationFormRepository
                .findByContractEndDateBeforeAndVerifiedIsTrueAndExpiredIsFalseAndVerificationFormType(now, 1);

        for (VerificationForm contract : expiredContracts) {
            processExpiredContract(contract);
        }
    }

    /**
     * Xử lý một hợp đồng đã hết hạn
     */
    private void processExpiredContract(VerificationForm contract) {
        User user = contract.getUser();
        if (user == null) return;

        // Tìm căn hộ liên quan
        Apartment apartment = apartmentRepository.findApartmentByApartmentName(contract.getApartmentName());
        if (apartment == null) return;

        // Gửi thông báo cho người thuê
        notificationService.createAndBroadcastNotification(
                String.format("Hợp đồng của bạn tại căn hộ %s đã hết hạn. Vui lòng liên hệ quản lý để gia hạn hoặc làm thủ tục trả căn hộ.",
                        contract.getApartmentName()),
                "Thông báo hết hợp đồng",
                user.getUserId()
        );

        // Gửi thông báo cho chủ sở hữu nếu có
        if (apartment.getHouseholder() != null) {
            User owner = userRepository.findByUserName(apartment.getHouseholder());
            if (owner != null) {
                notificationService.createAndBroadcastNotification(
                        String.format("Hợp đồng thuê căn hộ %s của người thuê %s đã hết hạn.",
                                apartment.getApartmentName(), user.getFullName()),
                        "Thông báo hết hợp đồng",
                        owner.getUserId()
                );
            }
        }

        // Xóa người thuê khỏi căn hộ
        if (user.getApartments().contains(apartment)) {
            user.getApartments().remove(apartment);
            apartment.getUsers().remove(user);
            userRepository.save(user);

            // Cập nhật số lượng người trong căn hộ
            if (apartment.getTotalNumber() > 0) {
                apartment.setTotalNumber(apartment.getTotalNumber() - 1);
            }

            // Nếu không còn ai thuê, đặt trạng thái là "sold" (chỉ còn chủ sở hữu)
            if (apartment.getUsers().stream().noneMatch(u -> "Rentor".equals(u.getRole()))) {
                apartment.setStatus("sold");
            }

            apartmentRepository.save(apartment);
        }

        // Đánh dấu hợp đồng đã xử lý
        contract.setExpired(true);      // Đánh dấu đã xử lý hết hạn
        contract.setVerified(false);    // Đánh dấu không còn hiệu lực
        verificationFormRepository.save(contract);
    }
}