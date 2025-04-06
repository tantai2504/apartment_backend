package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.BillResponseDTO;
import com.example.apartmentmanagement.dto.BillRequestDTO;
import com.example.apartmentmanagement.entities.Bill;

import java.util.List;

public interface BillService {

    List<BillResponseDTO> getAllBill();

    List<BillResponseDTO> getAllBillsWithinSpecTime(Long userId, int month, int year);

    BillResponseDTO getBillById(Long id);

    void processPaymentSuccess(Long billId, String paymentInfo);

    BillResponseDTO updateBill(Long id, BillRequestDTO billRequestDTO);

    List<BillResponseDTO> viewBillListWithinSpecTime(int month, int year, Long userId);

    List<BillResponseDTO> viewBillList(Long userId);

    List<BillResponseDTO> viewRentorBills(Long rentorId);

    void deleteBill(Long id);

    BillResponseDTO addBill(BillRequestDTO billRequestDTO);

    BillResponseDTO addBillConsumption(BillRequestDTO billRequestDTO);

    BillResponseDTO addBillMonthPaid(BillRequestDTO billRequestDTO);

    BillResponseDTO sendBillToRenter(BillRequestDTO billRequestDTO);
}
