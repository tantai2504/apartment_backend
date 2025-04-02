package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepositRequestDTO {
    private Long depositUserId;
    private Long postId;
    private float depositPrice;
    private String successUrl;
    private String cancelUrl;

    public DepositRequestDTO(Long depositUserId, Long postId, float depositPrice) {
        this.depositUserId = depositUserId;
        this.postId = postId;
        this.depositPrice = depositPrice;
    }
}
