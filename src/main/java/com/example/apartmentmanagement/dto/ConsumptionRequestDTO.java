package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsumptionRequestDTO {
    private LocalDate consumptionDate;
    private float lastMonthWaterConsumption;
    private float waterConsumption;
    private String userName;
    private String apartmentName;
}
