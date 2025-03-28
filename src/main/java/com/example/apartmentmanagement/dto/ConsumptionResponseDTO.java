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
    private float waterConsumption;
    private float electricConsumption;
    private UserResponseDTO user;

    public ConsumptionResponseDTO(Long id, LocalDateTime consumptionDate, float waterConsumption, float electricConsumption) {
        this.id = id;
        this.consumptionDate = consumptionDate;
        this.waterConsumption = waterConsumption;
        this.electricConsumption = electricConsumption;
    }
}
