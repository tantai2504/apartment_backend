package com.example.apartmentmanagement.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private Long billId;
    private float amount;
    private String paymentInfo;
    private Long userPaymentId;
}
