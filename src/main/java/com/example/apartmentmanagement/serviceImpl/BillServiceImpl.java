package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.entities.Bill;
import com.example.apartmentmanagement.repository.BillRepository;
import com.example.apartmentmanagement.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillServiceImpl implements BillService {

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
    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    @Override
    public void updateBill(Bill bill) {

    }

    @Override
    public String addBill(Bill bill) {
        return "";
    }

}
