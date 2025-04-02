package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.BillResponseDTO;
import com.example.apartmentmanagement.dto.ConsumptionResponseDTO;
import com.example.apartmentmanagement.service.ConsumptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/consumption")
public class ConsumptionController {
    @Autowired
    private ConsumptionService consumptionService;

    @GetMapping("/getAll")
    public ResponseEntity<Object> getAll() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ConsumptionResponseDTO> consumptions = consumptionService.getAll();
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", consumptions);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/getByMonthYear/{userId}")
    public ResponseEntity<Object> viewConsumption(@RequestParam int month, @RequestParam int year,
                                          @PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ConsumptionResponseDTO> consumptions = consumptionService.getAllConsumptionsByUser(month, year, userId);
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", consumptions);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/viewByMonthYear")
    public ResponseEntity<Object> viewAllConsumption(@RequestParam int month, @RequestParam int year) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ConsumptionResponseDTO> consumptions = consumptionService.viewAllConsumption(month, year);
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", consumptions);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/upload_file")
    public ResponseEntity<Object> uploadConsumptionFile(@RequestParam("file") MultipartFile file,
                                                        @RequestParam Long createdUserId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ConsumptionResponseDTO> consumptions = consumptionService.processExcelFile(file, createdUserId);
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", consumptions);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RuntimeException | IOException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
