package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.entities.Bill;

import java.util.List;

public interface BillService {

    float calculateElectricityBill(int kWh);

    List<Bill> getAllBills();

    void updateBill(Bill bill);

    String addBill(Bill bill);

}
