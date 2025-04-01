package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsumptionResponseDTO {
    private Long id;
    private LocalDateTime consumptionDate;
    private float lastMonthWaterConsumption;
    private float waterConsumption;
    private String userName;
    private String apartmentName;

    public ConsumptionResponseDTO(Long id, LocalDateTime consumptionDate, float waterConsumption, float lastMonthWaterConsumption, String userName) {
        this.id = id;
        this.consumptionDate = consumptionDate;
        this.waterConsumption = waterConsumption;
        this.lastMonthWaterConsumption = lastMonthWaterConsumption;
        this.userName = userName;
    }
}
