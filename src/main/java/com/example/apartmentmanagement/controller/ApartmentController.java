package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.ApartmentDTO;
import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.service.ApartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apartment")
public class ApartmentController {
    @Autowired
    private ApartmentService apartmentService;

    /**
     * View list can ho
     * @return List can ho
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<ApartmentDTO>> showAllApartment(){
        List<ApartmentDTO> apartments = apartmentService.showApartment();
        return ResponseEntity.ok(apartments);
    }

    /**
     * Tim kiem can ho
     *
     * @param name
     * @return Apartment
     */
    @GetMapping("/find")
    public ResponseEntity<Apartment> findApartmentByName(@RequestParam String name) {
        Apartment apartment = apartmentService.getApartmentByName(name);
        return ResponseEntity.ok(apartment);
    }

    @GetMapping("/getAll/unrented")
    public ResponseEntity<List<ApartmentDTO>> showAllUnrentedApartment() {
        List<ApartmentDTO> apartments = apartmentService.totalUnrentedApartment();
        if (apartments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(apartments);
    }

    /**
     * Tao moi can ho
     *
     * @param apartment
     * @param imageFile
     * @param bindingResult
     * @return
     */
    @PostMapping("/createApartment")
    public ResponseEntity<Object> createApartment(
            @Valid @ModelAttribute Apartment apartment,
            @RequestParam(value = "file", required = false) MultipartFile imageFile,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        String result = apartmentService.addApartment(apartment);
        if (result.equals("Add successfully")) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Add successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> findApartmentById(@PathVariable("id") Long id) {
        Apartment existed = apartmentService.getApartmentById(id);
        if (existed != null) {
            return ResponseEntity.status(HttpStatus.OK).body(existed);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find apartment");
        }
    }
}
