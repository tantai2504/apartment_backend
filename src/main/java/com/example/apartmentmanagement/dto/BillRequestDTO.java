package com.example.apartmentmanagement.dto;

import lombok.Data;

@Data
public class BillRequestDTO {
    private String userName;
    private String billContent;
    private int electricCons;
    private int waterCons;
    private float others;
}
