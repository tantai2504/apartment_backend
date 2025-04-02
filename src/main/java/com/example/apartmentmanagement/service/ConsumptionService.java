package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.ConsumptionResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ConsumptionService {
    List<ConsumptionResponseDTO> getAllConsumptionsByUser(int month, int year, Long userId);
    List<ConsumptionResponseDTO> getAll();
    List<ConsumptionResponseDTO> viewAllConsumption(int month, int year);
    List<ConsumptionResponseDTO> processExcelFile(MultipartFile file, Long createdUserId) throws IOException;
}
