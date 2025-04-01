package com.example.apartmentmanagement.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillRequestDTO {
    private String userName;
    private String billContent;
    private float monthlyPaid;
    private float managementFee;
    private int lastMonthWaterCons;
    private int waterCons;
    private float others;

    public BillRequestDTO(String userName, String billContent, float managementFee, int lastMonthWaterCons, int waterCons, float others) {
        this.userName = userName;
        this.billContent = billContent;
        this.managementFee = managementFee;
        this.lastMonthWaterCons = lastMonthWaterCons;
        this.waterCons = waterCons;
        this.others = others;
    }
}
