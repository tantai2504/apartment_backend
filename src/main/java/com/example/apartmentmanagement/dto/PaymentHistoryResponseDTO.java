package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryResponseDTO {
    private Long paymentId;
    private boolean paymentStatus;
    private LocalDateTime paymentDate;
    private String paymentInfo;
    private String paymentType;
    private Long userId;
    private Long billId;
}
