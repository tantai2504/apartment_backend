package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.BillDTO;
import com.example.apartmentmanagement.entities.Bill;

import java.util.List;

public interface BillService {

    float calculateElectricityBill(int kWh);

    List<BillDTO> getAllBillsWithinSpecTime(int month, int year);

    // Xem danh sach bill cua user da dang nhap vao
    List<BillDTO> viewBillList(int month, int year, Long userId);

    void updateBill(Bill bill);

    String addBill(Bill bill);

}
