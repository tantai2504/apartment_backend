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
public class ConsumptionResponseDTO {
    private Long id;
    private LocalDate consumptionDate;
    private float lastMonthWaterConsumption;
    private float waterConsumption;
    private String userName;
    private String apartmentName;
    private boolean isBillCreated;

    public ConsumptionResponseDTO(Long id, LocalDate consumptionDate, float lastMonthWaterConsumption, float waterConsumption, String userName, String apartmentName) {
        this.id = id;
        this.consumptionDate = consumptionDate;
        this.lastMonthWaterConsumption = lastMonthWaterConsumption;
        this.waterConsumption = waterConsumption;
        this.userName = userName;
        this.apartmentName = apartmentName;
    }
}
