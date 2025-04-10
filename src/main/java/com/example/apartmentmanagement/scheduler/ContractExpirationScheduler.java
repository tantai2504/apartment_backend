package com.example.apartmentmanagement.scheduler;

import com.example.apartmentmanagement.entities.VerificationForm;
import com.example.apartmentmanagement.repository.VerificationFormRepository;
import com.example.apartmentmanagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ContractExpirationScheduler {

    @Autowired
    private VerificationFormRepository verificationFormRepository;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkContractExpirations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekFromNow = now.plusWeeks(1);

        List<VerificationForm> expiringContracts = verificationFormRepository.findByContractEndDateBetweenAndVerifiedIsTrue(now, oneWeekFromNow);
        for (VerificationForm contract : expiringContracts) {
            // Gửi thông báo cho người dùng có hợp đồng sắp hết hạn
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
