package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.Resident;
import com.example.apartmentmanagement.service.ResidentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/resident")
public class ResidentController {

    @Autowired
    private ResidentService residentService;

    @PostMapping("/add/{apartmentId}")
    public ResponseEntity<Object> add(@Valid @ModelAttribute Resident resident, BindingResult bindingResult,
                                      @PathVariable Long apartmentId) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        residentService.addResident(resident, apartmentId);
        if (residentService.addResident(resident, apartmentId) == "Cannot found apartment") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot found apartment");
        } else if (residentService.addResident(resident, apartmentId) == "Cannot add more people") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot add more people");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Add successfully!");
    }
}
