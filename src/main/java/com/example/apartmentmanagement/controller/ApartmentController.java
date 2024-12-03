package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.service.ApartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apartment")
public class ApartmentController {
    @Autowired
    private ApartmentService apartmentService;

    @GetMapping("/getAll")
    public List<Apartment> showAllApartment(){
        return apartmentService.showApartment();
    }

    @GetMapping("/getAll/unrented")
    public List<Apartment> showAllUnrentedApartment(){
        return apartmentService.totalUnrentedApartment();
    }

    @PostMapping("/add")
    public ResponseEntity<Object> add(@Valid @RequestBody Apartment apartment, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        String checkResult = apartmentService.checkApartmentExisted(apartment);
        if (checkResult.equals("Existed apartment")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Existed apartment");
        } else {
            String result = apartmentService.addApartment(apartment);
            if (result.equals("Add successfully")) {
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
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

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateApartment(@PathVariable("id") Long id, @Valid @RequestBody Apartment apartment, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        Apartment existed = apartmentService.getApartmentById(id);
        if (existed != null) {
            apartmentService.updateApartment(existed, apartment);
            return ResponseEntity.status(HttpStatus.CREATED).body("Update successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot found apartment");
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Object> deleteApartment(@PathVariable("id") Long id){
        Apartment existed = apartmentService.getApartmentById(id);
        if (existed != null) {
            apartmentService.deleteApartment(id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Delete successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot found apartment");
        }
    }
}
