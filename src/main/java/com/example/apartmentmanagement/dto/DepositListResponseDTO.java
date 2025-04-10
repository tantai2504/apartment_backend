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
public class DepositListResponseDTO {
    private Long depositId;
    private String apartmentName;
    private String depositUserName;
    private float depositPrice;
    private String status;
    private String postOwnerName;
}