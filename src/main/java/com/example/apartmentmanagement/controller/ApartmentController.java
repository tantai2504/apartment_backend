package com.example.apartmentmanagement.controller;

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
    public List<Apartment> showAllApartment(){
        return apartmentService.showApartment();
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
    public List<Apartment> showAllUnrentedApartment(){
        return apartmentService.totalUnrentedApartment();
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

    /**
     * Them cu dan moi vao can ho
     *
     * @param apartment
     * @param userId
     * @param imageFile
     * @param bindingResult
     * @return
     */
    @PostMapping("/add")
    public ResponseEntity<Object> addResidentIntoApartment(
            @Valid @ModelAttribute Apartment apartment,
            @RequestParam(value = "user_id", required = false) Long userId,
            @RequestParam(value = "file", required = false) MultipartFile imageFile,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        String result = apartmentService.addResidentIntoApartment(apartment, userId, imageFile);
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

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateApartment(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute Apartment apartment,
            @RequestParam(value = "file", required = false) MultipartFile imageFile,
            BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        Apartment existed = apartmentService.getApartmentById(id);
        if (existed != null) {
            apartmentService.updateApartment(existed, apartment, imageFile);
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
