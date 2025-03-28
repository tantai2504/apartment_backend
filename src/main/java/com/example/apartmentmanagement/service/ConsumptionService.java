package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.ConsumptionResponseDTO;

import java.util.List;

public interface ConsumptionService {
    List<ConsumptionResponseDTO> getAllConsumptionsByUser(int month, int year, Long userId);
    List<ConsumptionResponseDTO> viewAllConsumption(int month, int year);
}
