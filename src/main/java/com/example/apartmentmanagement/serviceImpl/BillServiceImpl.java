package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.BillDTO;
import com.example.apartmentmanagement.entities.Bill;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.BillRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillRepository billRepository;

    @Override
    public float calculateElectricityBill(int kWh) {
        float total = 0;
        int remaining = kWh;

        int[] thresholds = {50, 50, 100, 100, 100};
        double[] prices = {1678, 1734, 2014, 2536, 2834, 2927};

        for (int i = 0; i < thresholds.length; i++) {
            if (remaining > 0) {
                int consumption = Math.min(remaining, thresholds[i]);
                total += consumption * prices[i];
                remaining -= consumption;
            }
        }
        if (remaining > 0) {
            total += remaining * prices[5];
        }
        return total;
    }

    @Override
    public List<BillDTO> getAllBillsWithinSpecTime(int month, int year) {
        List<Bill> bills = billRepository.findAll();

        return bills.stream()
                .filter(bill -> bill.getBillDate().getMonthValue() == month && bill.getBillDate().getYear() == year)
                .map(bill -> new BillDTO(
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
    public List<BillDTO> viewBillList(int month, int year, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getBills() == null) return List.of();

        return user.getBills().stream()
                .filter(bill -> bill.getBillDate().getMonthValue() == month && bill.getBillDate().getYear() == year)
                .map(bill -> new BillDTO(
                        bill.getBillId(),
                        bill.getBillContent(),
                        bill.getElectricBill(),
                        bill.getWaterBill(),
                        bill.getOthers(),
                        bill.getTotal(),
                        bill.getBillDate(),
                        bill.getStatus(),
                        user.getFullName(),
                        bill.getApartment().getApartmentName()
                ))
                .collect(Collectors.toList());
    }


    @Override
    public void updateBill(Bill bill) {

    }

    @Override
    public String addBill(Bill bill) {

        return null;
    }

}
