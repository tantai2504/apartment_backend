package com.example.apartmentmanagement.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillRequestDTO {
    private String apartmentName;
    private String billContent;
    private String userName;
    private Long consumptionId;
    private Long createdUserId;
    private float surcharge;
    private String period;
    private float amount;
}
