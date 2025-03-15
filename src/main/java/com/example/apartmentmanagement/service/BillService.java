package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.BillDTO;
import com.example.apartmentmanagement.dto.BillRequestDTO;
import com.example.apartmentmanagement.entities.Bill;

import java.util.List;

public interface BillService {

    List<BillDTO> getAllBillsWithinSpecTime(Long userId, int month, int year);

    BillDTO getBillById(Long id);

    BillRequestDTO updateBill(Long id, BillRequestDTO billRequestDTO);

    List<BillDTO> viewBillList(int month, int year, Long userId);

    void deleteBill(Long id);

    String addBill(String billContent, String userName, int electricCons, int waterCons, float otherCost);

}
