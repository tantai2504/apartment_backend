package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.BillResponseDTO;
import com.example.apartmentmanagement.dto.BillRequestDTO;
import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.Bill;
import com.example.apartmentmanagement.entities.Payment;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.ApartmentRepository;
import com.example.apartmentmanagement.repository.BillRepository;
import com.example.apartmentmanagement.repository.PaymentRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.BillService;
import com.example.apartmentmanagement.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public List<BillResponseDTO> getAllBillsWithinSpecTime(Long userId, int month, int year) {
        User user = userRepository.findById(userId).get();
        List<Bill> bills = user.getBills();

        return bills.stream()
                .filter(bill -> bill.getBillDate().getMonthValue() == month && bill.getBillDate().getYear() == year)
                .map(bill -> new BillResponseDTO(
                        bill.getBillId(),
                        bill.getBillContent(),
                        bill.getElectricBill(),
                        bill.getWaterBill(),
                        bill.getOthers(),
                        bill.getTotal(),
                        bill.getBillDate(),
                        bill.getStatus(),
                        bill.getUser().getFullName(),
                        bill.getApartment().getApartmentName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public BillResponseDTO getBillById(Long id) {
        Bill bill = billRepository.findById(id).get();
        BillResponseDTO billResponseDTO = new BillResponseDTO(
                bill.getBillId(),
                bill.getBillContent(),
                bill.getElectricBill(),
                bill.getWaterBill(),
                bill.getOthers(),
                bill.getTotal(),
                bill.getBillDate(),
                bill.getStatus(),
                bill.getUser().getUserName(),
                bill.getApartment().getApartmentName()
        );
        return billResponseDTO;
    }

    @Override
    public void processPaymentSuccess(Long billId, String paymentInfo) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn"));

        if ("paid".equalsIgnoreCase(bill.getStatus())) {
            throw new IllegalStateException("Hóa đơn này đã được thanh toán");
        }

        // Tạo bản ghi Payment
        Payment payment = new Payment();
        payment.setPaymentCheck(true);
        payment.setPaymentInfo(paymentInfo);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setUser(bill.getUser());
        payment.setBill(bill);
        payment.setPaymentType("bill");
        paymentRepository.save(payment);

        bill.setStatus("paid");
        bill.setPayment(payment);

        billRepository.save(bill);
    }


    @Override
    public BillResponseDTO updateBill(Long id, BillRequestDTO billRequestDTO) {
        Bill bill = billRepository.findById(id).get();
        bill.setBillContent(billRequestDTO.getBillContent());
        bill.setOthers(billRequestDTO.getOthers());
        bill.setBillDate(LocalDateTime.now());
        billRepository.save(bill);
        return new BillResponseDTO(
                bill.getBillId(),
                bill.getBillContent(),
                bill.getElectricBill(),
                bill.getWaterBill(),
                bill.getOthers(),
                bill.getTotal(),
                bill.getBillDate(),
                bill.getStatus(),
                bill.getUser().getUserName(),
                bill.getApartment().getApartmentName()
        );
    }

    @Override
    public List<BillResponseDTO> viewBillList(int month, int year, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getBills() == null) return List.of();

        return user.getBills().stream()
                .filter(bill -> bill.getBillDate().getMonthValue() == month && bill.getBillDate().getYear() == year)
                .map(bill -> new BillResponseDTO(
                        bill.getBillId(),
                        bill.getBillContent(),
                        bill.getElectricBill(),
                        bill.getWaterBill(),
                        bill.getOthers(),
                        bill.getTotal(),
                        bill.getBillDate(),
                        bill.getStatus(),
                        user.getUserName(),
                        bill.getApartment().getApartmentName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBill(Long id) {
        Bill bill = billRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bill này"));
        if (bill.getStatus().equals("unpaid")) {
            throw new RuntimeException("Phải thanh toán hoá đơn trước");
        } else {
            billRepository.delete(bill);
        }
    }

    @Override
    public String addBill(String billContent, String name, int electricCons, int waterCons, float otherCost) {
        System.out.println("bill content" + billContent);
        User user = userRepository.findByUserName(name);

        List<Apartment> apartments = user.getApartments();

        Bill newBill = new Bill();

        float electricCost = calculateElectricBill(electricCons);
        newBill.setElectricBill(electricCost);
        newBill.setBillContent(billContent);

        float waterCost = calculateWaterBill(waterCons);
        newBill.setWaterBill(waterCost);
        newBill.setOthers(otherCost);
        newBill.setTotal(electricCost + waterCost + otherCost);

        newBill.setBillDate(LocalDateTime.now());
        newBill.setStatus("unpaid");

        newBill.setUser(user);

        for (Apartment apartment : apartments) {
            newBill.setApartment(apartment);
        }

        String notificationContent = "Thông báo hóa đơn mới";

        notificationService.createNotification(notificationContent, "1", user.getUserId());

        billRepository.save(newBill);
        return "success";
    }
    private float calculateElectricBill(int consumption) {
        float total = 0;
        int remaining = consumption;

        int[] thresholds = {50, 50, 100, 100, 100};
        float[] rates = {1.678F, 1.734F, 2.014F, 2.536F, 2.834F, 2.927F};

        for (int i = 0; i < thresholds.length; i++) {
            if (remaining > thresholds[i]) {
                total += thresholds[i] * rates[i];
                remaining -= thresholds[i];
            } else {
                total += remaining * rates[i];
                return total;
            }
        }

        // Nếu còn điện năng tiêu thụ vượt bậc cuối cùng
        if (remaining > 0) {
            total += remaining * rates[rates.length - 1];
        }

        return total;
    }

    private float calculateWaterBill(int consumption) {
        float total = 0;
        int remaining = consumption;

        int[] thresholds = {10, 10, 10};
        float[] rates = {5.973F, 7.052F, 8.669F, 15.929F};

        for (int i = 0; i < thresholds.length; i++) {
            if (remaining > thresholds[i]) {
                total += thresholds[i] * rates[i];
                remaining -= thresholds[i];
            } else {
                total += remaining * rates[i];
                return total;
            }
        }

        if (remaining > 0) {
            total += remaining * rates[rates.length - 1];
        }

        return total;
    }

}
