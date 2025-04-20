package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReCoinUpdateRequestDTO {
//    private Long userId;
    private Long reCoinId;
    private String imgBill;
    private String reason;
}
