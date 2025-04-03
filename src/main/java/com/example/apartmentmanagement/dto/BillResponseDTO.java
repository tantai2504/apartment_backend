package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillResponseDTO {
    private Long billId;
    private String billContent;
    private float monthlyPaid;
    private float waterBill;
    private float others;
    private float total;
    private float lastMonthWaterConsumption;
    private float waterConsumption;
    private LocalDateTime billDate;
    private String status;
    private String username;
    private String apartmentName;
}