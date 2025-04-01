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
                        bill.getMonthlyPaid(),
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
                bill.getMonthlyPaid(),
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
                bill.getMonthlyPaid(),
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
                        bill.getMonthlyPaid(),
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
    public BillResponseDTO addBill(String billContent, String name, float managementFee, int lastMonthCons, int waterCons, float otherCost) {
        System.out.println("bill content" + billContent);
        User user = userRepository.findByUserName(name);

        List<Apartment> apartments = user.getApartments();

        Bill newBill = new Bill();

        newBill.setManagementFee(managementFee);
        newBill.setBillContent(billContent);

        float waterCost = (float) calculateWaterBill(lastMonthCons, waterCons);
        newBill.setWaterBill(waterCost);
        newBill.setOthers(otherCost);
        newBill.setTotal(managementFee + waterCost + otherCost);

        newBill.setBillDate(LocalDateTime.now());
        newBill.setStatus("unpaid");

        newBill.setUser(user);

        for (Apartment apartment : apartments) {
            newBill.setApartment(apartment);
        }

        String notificationContent = "Thông báo hóa đơn mới";

        notificationService.createNotification(notificationContent, "1", user.getUserId());

        billRepository.save(newBill);
        return new BillResponseDTO(
                newBill.getBillId(),
                newBill.getBillContent(),
                newBill.getMonthlyPaid(),
                newBill.getWaterBill(),
                newBill.getOthers(),
                newBill.getTotal(),
                newBill.getBillDate(),
                newBill.getStatus(),
                newBill.getUser().getUserName(),
                newBill.getApartment().getApartmentName()
        );
    }

    @Override
    public BillResponseDTO sendBillToRenter(String billContent, float waterCons, float monthlyPaid, float managementFee, String userName) {
        return null;
    }

    public double calculateWaterBill(int lastMonthWaterConsumption, int waterConsumption) {
        double totalCost = 0;

        int monthlyWaterPaid = waterConsumption - lastMonthWaterConsumption;

        if (monthlyWaterPaid <= 10) {
            totalCost = waterConsumption * 10000;
        } else if (monthlyWaterPaid <= 20) {
            totalCost = (10 * 10000) + (monthlyWaterPaid - 10) * 12000;
        } else if (monthlyWaterPaid <= 30) {
            totalCost = (10 * 10000) + (10 * 12000) + (monthlyWaterPaid - 20) * 15000;
        } else {
            totalCost = (10 * 10000) + (10 * 12000) + (10 * 15000) + (monthlyWaterPaid - 30) * 18000;
        }

        return totalCost;
    }

}
