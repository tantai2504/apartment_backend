package com.example.apartmentmanagement.type;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreatePaymentLinkRequestBody {
    private Long billId;
    private String productName;
    private String description;
    private String returnUrl;
    private String cancelUrl;
    private int price;
}