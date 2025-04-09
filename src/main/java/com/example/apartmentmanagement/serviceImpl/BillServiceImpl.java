package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.BillResponseDTO;
import com.example.apartmentmanagement.dto.BillRequestDTO;
import com.example.apartmentmanagement.dto.PaymentRequestDTO;
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
        return billRepository.findAll().stream()
                .map(bill -> new BillResponseDTO(
                        bill.getBillId(),
                        bill.getBillContent(),
                        bill.getAmount(),
                        bill.getBillDate(),
                        bill.getStatus(),
                        bill.getUser().getFullName(),
                        bill.getApartment().getApartmentName(),
                        bill.getBillType(),
                        bill.getSurcharge(),
                        bill.getCreateBillUserId(),
                        bill.getApartment().getStatus(),
                        bill.getPeriod(),
                        bill.getConsumption()
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
                        bill.getAmount(),
                        bill.getBillDate(),
                        bill.getStatus(),
                        bill.getUser().getFullName(),
                        bill.getApartment().getApartmentName(),
                        bill.getBillType(),
                        bill.getSurcharge(),
                        bill.getCreateBillUserId(),
                        bill.getApartment().getStatus(),
                        bill.getPeriod(),
                        bill.getConsumption()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public BillResponseDTO getBillById(Long id) {
        Bill bill = billRepository.findById(id).get();
        BillResponseDTO billResponseDTO = new BillResponseDTO(
                bill.getBillId(),
                bill.getBillContent(),
                bill.getAmount(),
                bill.getBillDate(),
                bill.getStatus(),
                bill.getUser().getFullName(),
                bill.getApartment().getApartmentName(),
                bill.getBillType(),
                bill.getSurcharge(),
                bill.getCreateBillUserId(),
                bill.getApartment().getStatus(),
                bill.getPeriod(),
                bill.getConsumption()
        );
        return billResponseDTO;
    }

    @Override
    public void processPaymentSuccess(PaymentRequestDTO paymentRequestDTO) {
        Bill bill = billRepository.findById(paymentRequestDTO.getBillId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn"));

        User userPayment = userRepository.findById(paymentRequestDTO.getUserPaymentId()).get();
        User owner = bill.getUser();

        if (bill.getStatus().equalsIgnoreCase("paid")) {
            throw new IllegalStateException("Hóa đơn này đã được thanh toán");
        }

        if(userPayment.getUserId() != owner.getUserId() && bill.getBillType().equalsIgnoreCase("monthPaid")){
            owner.setAccountBalance(owner.getAccountBalance()+bill.getAmount());
            userRepository.save(owner);
        }
        // Tạo bản ghi Payment
        Payment payment = new Payment();
        payment.setPaymentCheck(true);
        payment.setPaymentInfo(paymentRequestDTO.getPaymentInfo());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setUser(userPayment);
        payment.setBill(bill);
        payment.setPrice(paymentRequestDTO.getAmount());
        payment.setPaymentType("bill");
        Payment savedPayment = paymentRepository.save(payment);

        bill.setStatus("paid");
        bill.setPayment(savedPayment);
        billRepository.save(bill);
    }


    @Override
    public BillResponseDTO updateBill(Long id, BillRequestDTO billRequestDTO) {
        Bill bill = billRepository.findById(id).get();
        bill.setBillContent(billRequestDTO.getBillContent());
        bill.setBillDate(LocalDateTime.now());
        billRepository.save(bill);
        return new BillResponseDTO(
                bill.getBillId(),
                bill.getBillContent(),
                bill.getAmount(),
                bill.getBillDate(),
                bill.getStatus(),
                bill.getUser().getFullName(),
                bill.getApartment().getApartmentName(),
                bill.getBillType(),
                bill.getSurcharge(),
                bill.getCreateBillUserId(),
                bill.getApartment().getStatus(),
                bill.getPeriod(),
                bill.getConsumption()
        );
    }

    @Override
    public List<BillResponseDTO> viewBillListWithinSpecTime(int month, int year, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getBills() == null) return List.of();

        return user.getBills().stream()
                .filter(bill -> bill.getConsumption().getConsumptionDate().getMonthValue() == month && bill.getConsumption().getConsumptionDate().getYear() == year)
                .map(bill -> new BillResponseDTO(
                        bill.getBillId(),
                        bill.getBillContent(),
                        bill.getAmount(),
                        bill.getBillDate(),
                        bill.getStatus(),
                        user.getUserName(),
                        bill.getApartment().getApartmentName(),
                        bill.getBillType(),
                        bill.getSurcharge(),
                        bill.getCreateBillUserId(),
                        bill.getApartment().getStatus(),
                        bill.getPeriod(),
                        bill.getConsumption()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<BillResponseDTO> viewBillList(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getBills() == null) return List.of();
        List<Bill> bills = billRepository.findBillByUser(user);
        return user.getBills().stream()
                .map(bill -> new BillResponseDTO(
                        bill.getBillId(),
                        bill.getBillContent(),
                        bill.getAmount(),
                        bill.getBillDate(),
                        bill.getStatus(),
                        user.getUserName(),
                        bill.getApartment().getApartmentName(),
                        bill.getBillType(),
                        bill.getSurcharge(),
                        bill.getCreateBillUserId(),
                        bill.getApartment().getStatus(),
                        bill.getPeriod(),
                        bill.getConsumption()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<BillResponseDTO> viewRentorBills(Long rentorId) {
        List<BillResponseDTO> lists = new ArrayList<>();
        List<Bill> bills = billRepository.findAll();
        for(Bill bill: bills){
            if(bill.getApartment().getStatus().equalsIgnoreCase("unrent")){
                continue;
            }
            List<User> users = bill.getApartment().getUsers();
            for(User u : users){
                if(u.getUserId() == rentorId){
                    lists.add(new BillResponseDTO(
                            bill.getBillId(),
                            bill.getBillContent(),
                            bill.getAmount(),
                            bill.getBillDate(),
                            bill.getStatus(),
                            bill.getUser().getFullName(),
                            bill.getApartment().getApartmentName(),
                            bill.getBillType(),
                            bill.getSurcharge(),
                            bill.getCreateBillUserId(),
                            bill.getApartment().getStatus(),
                            bill.getPeriod(),
                            bill.getConsumption()
                    ));
                }
            }
        }
        return lists;
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
        return null;
    }

    @Override
    public BillResponseDTO addBillConsumption(BillRequestDTO billRequestDTO) {
        Consumption consumption = consumptionRepository.findById(billRequestDTO.getConsumptionId()).orElse(null);
        if (consumption.isBillCreated()) {
            throw new RuntimeException("Đã tạo hoá đơn này");
        }

        Apartment apartment = apartmentRepository.findApartmentByApartmentName(billRequestDTO.getApartmentName());
        if (apartment == null) {
            throw new RuntimeException("Không tìm thấy căn hộ này");
        }
        User user = userRepository.findByUserNameOrEmail(apartment.getHouseholder());
        if (user == null) {
            throw new RuntimeException("Không tìm thấy chủ căn hộ này");
        }
        Bill newBill = new Bill();
        newBill.setBillContent(billRequestDTO.getBillContent());
        newBill.setBillType("water");
        float waterCost = calculateWaterBill(consumption.getLastMonthWaterConsumption(), consumption.getWaterConsumption());
        newBill.setAmount(waterCost);
        newBill.setBillDate(LocalDateTime.now());
        newBill.setStatus("unpaid");
        newBill.setUser(user);
        newBill.setCreateBillUserId(billRequestDTO.getCreatedUserId());
        newBill.setConsumption(consumption);
        newBill.setApartment(apartment);
        newBill.setSurcharge(billRequestDTO.getSurcharge());

        consumption.setBillCreated(true);
        consumptionRepository.save(consumption);

        billRepository.save(newBill);
        return new BillResponseDTO(
                newBill.getBillId(),
                newBill.getBillContent(),
                newBill.getAmount(),
                newBill.getBillDate(),
                newBill.getStatus(),
                newBill.getUser().getUserName(),
                newBill.getApartment().getApartmentName(),
                newBill.getBillType(),
                newBill.getSurcharge(),
                newBill.getCreateBillUserId(),
                newBill.getApartment().getStatus(),
                newBill.getPeriod(),
                newBill.getConsumption()
        );
    }

    @Override
    public BillResponseDTO addBillMonthPaid(BillRequestDTO billRequestDTO) {
//        Consumption consumption = consumptionRepository.findById(billRequestDTO.getConsumptionId()).orElse(null);
//        if (consumption.isBillCreated()) {
////            List<Bill> bills = consumption.getBills();
////            for (Bill bill : bills) {
////                if (bill.getBillType().equals("water")) {
////                    throw new RuntimeException("Đã tạo hoá đơn cho toà nhà này");
////                }
////            }
//            throw new RuntimeException("Đã tạo hoá đơn này");
//        }

        Apartment apartment = apartmentRepository.findApartmentByApartmentName(billRequestDTO.getApartmentName());
        if (apartment == null) {
            throw new RuntimeException("Không tìm thấy căn hộ này");
        }
        User user = userRepository.findByUserNameOrEmail(apartment.getHouseholder());
        if (user == null) {
            throw new RuntimeException("Không tìm thấy chủ căn hộ này");
        }
        Bill newBill = new Bill();
        newBill.setBillContent(billRequestDTO.getBillContent());
        newBill.setBillType("monthPaid");
//        float waterCost = calculateWaterBill(consumption.getLastMonthWaterConsumption(), consumption.getWaterConsumption());
        newBill.setAmount(billRequestDTO.getAmount());
        newBill.setBillDate(LocalDateTime.now());
        newBill.setStatus("unpaid");
        newBill.setUser(user);
        newBill.setCreateBillUserId(billRequestDTO.getCreatedUserId());
        newBill.setConsumption(null);
        newBill.setApartment(apartment);
        newBill.setSurcharge(billRequestDTO.getSurcharge());

        billRepository.save(newBill);
        return new BillResponseDTO(
                newBill.getBillId(),
                newBill.getBillContent(),
                newBill.getAmount(),
                newBill.getBillDate(),
                newBill.getStatus(),
                newBill.getUser().getUserName(),
                newBill.getApartment().getApartmentName(),
                newBill.getBillType(),
                newBill.getSurcharge(),
                newBill.getCreateBillUserId(),
                newBill.getApartment().getStatus(),
                newBill.getPeriod(),
                newBill.getConsumption()
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

        newBill.setBillContent(billRequestDTO.getBillContent());

        float waterCost = calculateWaterBill(consumption.getLastMonthWaterConsumption(), consumption.getWaterConsumption());

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
                newBill.getAmount(),
                newBill.getBillDate(),
                newBill.getStatus(),
                newBill.getUser().getUserName(),
                newBill.getApartment().getApartmentName(),
                newBill.getBillType(),
                newBill.getSurcharge(),
                newBill.getCreateBillUserId(),
                newBill.getApartment().getStatus(),
                newBill.getPeriod(),
                newBill.getConsumption()
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
