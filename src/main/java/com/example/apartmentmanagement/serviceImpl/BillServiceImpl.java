package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.BillResponseDTO;
import com.example.apartmentmanagement.dto.BillRequestDTO;
import com.example.apartmentmanagement.entities.*;
import com.example.apartmentmanagement.repository.*;
import com.example.apartmentmanagement.service.BillService;
import com.example.apartmentmanagement.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private ConsumptionRepository consumptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public List<BillResponseDTO> getAllBill() {
        return billRepository.findAll().stream().filter(bill -> "owner_bill".equals(bill.getBillType()))
                .map(bill -> new BillResponseDTO(
                        bill.getBillId(),
                        bill.getBillContent(),
                        bill.getMonthlyPaid(),
                        bill.getWaterBill(),
                        bill.getOthers(),
                        bill.getTotal(),
                        bill.getConsumption().getLastMonthWaterConsumption(),
                        bill.getConsumption().getWaterConsumption(),
                        bill.getBillDate(),
                        bill.getStatus(),
                        bill.getUser().getFullName(),
                        bill.getApartment().getApartmentName(),
                        bill.getBillType(),
                        bill.getSurcharge(),
                        bill.getCreateBillUserId()
                ))
                .collect(Collectors.toList());

    }

    @Override
    public List<BillResponseDTO> getAllBillsWithinSpecTime(Long userId, int month, int year) {
        User user = userRepository.findById(userId).get();
        List<Bill> bills = user.getBills();

        return bills.stream()
                .filter(bill -> bill.getConsumption().getConsumptionDate().getMonthValue() == month && bill.getConsumption().getConsumptionDate().getYear() == year)
                .map(bill -> new BillResponseDTO(
                        bill.getBillId(),
                        bill.getBillContent(),
                        bill.getMonthlyPaid(),
                        bill.getWaterBill(),
                        bill.getOthers(),
                        bill.getTotal(),
                        bill.getConsumption().getLastMonthWaterConsumption(),
                        bill.getConsumption().getWaterConsumption(),
                        bill.getBillDate(),
                        bill.getStatus(),
                        bill.getUser().getFullName(),
                        bill.getApartment().getApartmentName(),
                        bill.getBillType(),
                        bill.getSurcharge(),
                        bill.getCreateBillUserId()
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
                bill.getConsumption().getLastMonthWaterConsumption(),
                bill.getConsumption().getWaterConsumption(),
                bill.getBillDate(),
                bill.getStatus(),
                bill.getUser().getUserName(),
                bill.getApartment().getApartmentName(),
                bill.getBillType(),
                bill.getSurcharge(),
                bill.getCreateBillUserId()
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
                bill.getConsumption().getLastMonthWaterConsumption(),
                bill.getConsumption().getWaterConsumption(),
                bill.getBillDate(),
                bill.getStatus(),
                bill.getUser().getUserName(),
                bill.getApartment().getApartmentName(),
                bill.getBillType(),
                bill.getSurcharge(),
                bill.getCreateBillUserId()
        );
    }

    @Override
    public List<BillResponseDTO> viewBillList(int month, int year, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getBills() == null) return List.of();

        return user.getBills().stream()
                .filter(bill -> bill.getConsumption().getConsumptionDate().getMonthValue() == month && bill.getConsumption().getConsumptionDate().getYear() == year)
                .map(bill -> new BillResponseDTO(
                        bill.getBillId(),
                        bill.getBillContent(),
                        bill.getMonthlyPaid(),
                        bill.getWaterBill(),
                        bill.getOthers(),
                        bill.getTotal(),
                        bill.getConsumption().getLastMonthWaterConsumption(),
                        bill.getConsumption().getWaterConsumption(),
                        bill.getBillDate(),
                        bill.getStatus(),
                        user.getUserName(),
                        bill.getApartment().getApartmentName(),
                        bill.getBillType(),
                        bill.getSurcharge(),
                        bill.getCreateBillUserId()
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
    public BillResponseDTO addBill(BillRequestDTO billRequestDTO) {
        Consumption consumption = consumptionRepository.findById(billRequestDTO.getConsumptionId()).orElse(null);
        if (consumption.isBillCreated()) {
            List<Bill> bills = consumption.getBills();
            for (Bill bill : bills) {
                if (bill.getBillType().equals("owner_bill")) {
                    throw new RuntimeException("Đã tạo hoá đơn cho toà nhà này");
                }
            }
        }

        Apartment apartment = apartmentRepository.findApartmentByApartmentName(billRequestDTO.getApartmentName());
        if (apartment == null) {
            throw new RuntimeException("Không tìm thấy căn hộ này");
        }

        User user = userRepository.findByUserNameOrEmail(apartment.getHouseholder());
        if (user == null) {
            throw new RuntimeException("Không tìm thấy chủ hộ");
        }

        Bill newBill = new Bill();

        newBill.setManagementFee(billRequestDTO.getManagementFee());
        newBill.setBillContent(billRequestDTO.getBillContent());

        float waterCost = calculateWaterBill(consumption.getLastMonthWaterConsumption(), consumption.getWaterConsumption());
        newBill.setWaterBill(waterCost);
        newBill.setOthers(billRequestDTO.getOthers());
        newBill.setTotal(billRequestDTO.getManagementFee() + waterCost + billRequestDTO.getOthers());

        newBill.setBillDate(LocalDateTime.now());
        newBill.setStatus("unpaid");
        newBill.setUser(user);
        newBill.setCreateBillUserId(billRequestDTO.getCreatedUserId());
        newBill.setConsumption(consumption);
        newBill.setApartment(apartment);
        newBill.setBillType("owner_bill");
        newBill.setSurcharge(billRequestDTO.getSurcharge());

        consumption.setBillCreated(true);
        consumptionRepository.save(consumption);

        billRepository.save(newBill);
        return new BillResponseDTO(
                newBill.getBillId(),
                newBill.getBillContent(),
                newBill.getMonthlyPaid(),
                newBill.getWaterBill(),
                newBill.getOthers(),
                newBill.getTotal(),
                newBill.getConsumption().getLastMonthWaterConsumption(),
                newBill.getConsumption().getWaterConsumption(),
                newBill.getBillDate(),
                newBill.getStatus(),
                newBill.getUser().getUserName(),
                newBill.getApartment().getApartmentName(),
                newBill.getBillType(),
                newBill.getSurcharge(),
                newBill.getCreateBillUserId()
        );
    }

    @Override
    public BillResponseDTO sendBillToRenter(BillRequestDTO billRequestDTO) {
        Consumption consumption = consumptionRepository.findById(billRequestDTO.getConsumptionId()).orElse(null);

        Apartment apartment = apartmentRepository.findApartmentByApartmentName(billRequestDTO.getApartmentName());
        if (apartment == null) {
            throw new RuntimeException("Không tìm thấy căn hộ này");
        }
        User user = userRepository.findByUserName(billRequestDTO.getUserName());

        Bill newBill = new Bill();

        newBill.setManagementFee(billRequestDTO.getManagementFee());
        newBill.setBillContent(billRequestDTO.getBillContent());

        float waterCost = calculateWaterBill(consumption.getLastMonthWaterConsumption(), consumption.getWaterConsumption());
        newBill.setWaterBill(waterCost);
        newBill.setMonthlyPaid(billRequestDTO.getMonthlyPaid());
        newBill.setTotal(billRequestDTO.getManagementFee() + waterCost + billRequestDTO.getMonthlyPaid());

        newBill.setBillDate(LocalDateTime.now());
        newBill.setStatus("unpaid");
        newBill.setUser(user);
        newBill.setCreateBillUserId(billRequestDTO.getCreatedUserId());
        newBill.setApartment(apartment);
        newBill.setBillType("rentor_bill");
        newBill.setSurcharge(billRequestDTO.getSurcharge());
        newBill.setConsumption(consumption);

        billRepository.save(newBill);
        return new BillResponseDTO(
                newBill.getBillId(),
                newBill.getBillContent(),
                newBill.getMonthlyPaid(),
                newBill.getWaterBill(),
                newBill.getOthers(),
                newBill.getTotal(),
                consumption.getLastMonthWaterConsumption(),
                consumption.getWaterConsumption(),
                newBill.getBillDate(),
                newBill.getStatus(),
                newBill.getUser().getUserName(),
                newBill.getApartment().getApartmentName(),
                newBill.getBillType(),
                newBill.getSurcharge(),
                newBill.getCreateBillUserId()
        );
    }

    public static float calculateWaterBill(float lastMonthWater, float waterConsumption) {
        float totalCost = 0;

        float monthlyWaterPaid = waterConsumption - lastMonthWater;
        if (monthlyWaterPaid <= 10) {
            totalCost = monthlyWaterPaid * 10000;
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
