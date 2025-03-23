package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.BillResponseDTO;
import com.example.apartmentmanagement.dto.BillRequestDTO;

import java.util.List;

public interface BillService {

    List<BillResponseDTO> getAllBillsWithinSpecTime(Long userId, int month, int year);

    BillResponseDTO getBillById(Long id);

    void processPaymentSuccess(Long billId, String paymentInfo);

    BillResponseDTO updateBill(Long id, BillRequestDTO billRequestDTO);

    List<BillResponseDTO> viewBillList(int month, int year, Long userId);

    void deleteBill(Long id);

    String addBill(String billContent, String userName, int electricCons, int waterCons, float otherCost);

}
