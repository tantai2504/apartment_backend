package com.example.apartmentmanagement.dto;

import com.example.apartmentmanagement.entities.Consumption;
import com.example.apartmentmanagement.entities.Payment;
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
    private float amount;
    private float lastMonthWaterConsumption;
    private float waterConsumption;
    private LocalDateTime billDate;
    private String status;
    private String username;
    private String apartmentName;
    private String billType;
    private float surcharge;
    private Long createBillUserId;
    private String apartmentStatus;
    private String period;
    private LocalDateTime paymentDate;
    private Long userPaymentId;
    private String userPaymentName;

    public BillResponseDTO(Long billId, String billContent, float amount, LocalDateTime billDate, String status, String username, String apartmentName, String billType, float surcharge, Long createBillUserId, String apartmentStatus, String period, Consumption consumption, Payment payment) {
        this.billId = billId;
        this.billContent = billContent;
        this.amount = amount;
        this.billDate = billDate;
        this.status = status;
        this.username = username;
        this.apartmentName = apartmentName;
        this.billType = billType;
        this.surcharge = surcharge;
        this.createBillUserId = createBillUserId;
        this.apartmentStatus = apartmentStatus;
        this.period = period;
        if(consumption != null){
            this.lastMonthWaterConsumption = consumption.getLastMonthWaterConsumption();
            this.waterConsumption = consumption.getWaterConsumption();
        }
        if(payment != null){
            this.paymentDate = payment.getPaymentDate();
            this.userPaymentName = payment.getUser().getUserName();
            this.userPaymentId = payment.getUser().getUserId();
        }
    }
}