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
    private int lastMonthWaterConsumption;
    private int waterConsumption;
    private String userName;
    private String apartmentName;

    public ConsumptionResponseDTO(Long id, LocalDateTime consumptionDate, int waterConsumption, int lastMonthWaterConsumption, String userName) {
        this.id = id;
        this.consumptionDate = consumptionDate;
        this.lastMonthWaterConsumption = waterConsumption;
        this.waterConsumption = waterConsumption;
        this.userName = userName;
    }
}
