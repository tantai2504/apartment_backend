package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.BillResponseDTO;
import com.example.apartmentmanagement.dto.ConsumptionResponseDTO;
import com.example.apartmentmanagement.service.ConsumptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/consumption")
public class ConsumptionController {
    @Autowired
    private ConsumptionService consumptionService;

    @GetMapping("/getAll/{userId}")
    public ResponseEntity<Object> viewConsumption(@RequestParam int month, @RequestParam int year,
                                          @PathVariable Long userId) {
        List<ConsumptionResponseDTO> consumptions = consumptionService.getAllConsumptionsByUser(month, year, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("data", consumptions);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/viewAll")
    public ResponseEntity<Object> viewAllConsumption(@RequestParam int month, @RequestParam int year) {
        List<ConsumptionResponseDTO> consumptions = consumptionService.viewAllConsumption(month, year);
        Map<String, Object> response = new HashMap<>();
        response.put("data", consumptions);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}
