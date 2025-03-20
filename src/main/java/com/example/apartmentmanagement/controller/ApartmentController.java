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
    public ResponseEntity<Object> showAllApartment(){
        List<ApartmentDTO> apartments = apartmentService.showApartment();
        Map<String, Object> response = new HashMap<>();
        if (apartments.isEmpty()) {
            response.put("message", "Không có căn hộ nào");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("data", apartments);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    /**
     * Tim kiem can ho
     *
     * @param name
     * @return Apartment
     */
    @GetMapping("/find")
    public ResponseEntity<Object> findApartmentByName(@RequestParam String name) {
        List<ApartmentDTO> apartment = apartmentService.getApartmentByName(name);
        Map<String, Object> response = new HashMap<>();
        if (apartment.isEmpty()) {
            response.put("message", "Không tìm thấy căn hộ này");
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("data", apartment);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAll/unrented")
    public ResponseEntity<Object> showAllUnrentedApartment() {
        List<ApartmentDTO> apartments = apartmentService.totalUnrentedApartment();
        Map<String, Object> response = new HashMap<>();
        if (apartments.isEmpty()) {
            response.put("message", "Tất cả các căn hộ đều đã được cho thuê");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("data", apartments);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get_own_apartment/")
    public ResponseEntity<Object> getOwnApartment(@RequestParam Long userId) {
        List<ApartmentDTO> apartments = apartmentService.totalUnrentedApartment();
        Map<String, Object> response = new HashMap<>();
        if (apartments.isEmpty()) {
            response.put("message", "Tất cả các căn hộ đều đã được cho thuê");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("data", apartments);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

//
//    /**
//     * Tao moi can ho
//     *
//     * @param apartment
//     * @param imageFile
//     * @param bindingResult
//     * @return
//     */
//    @PostMapping("/createApartment")
//    public ResponseEntity<Object> createApartment(
//            @Valid @ModelAttribute Apartment apartment,
//            @RequestParam(value = "file", required = false) MultipartFile imageFile,
//            BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            Map<String, String> errors = new HashMap<>();
//            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
//        }
//        String result = apartmentService.addApartment(apartment);
//        if (result.equals("Add successfully")) {
//            return ResponseEntity.status(HttpStatus.CREATED).body("Add successfully");
//        } else {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
//        }
//    }

    /**
     * Tìm kiếm căn hộ theo id
     *
     * @param id
     * @return
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Object> findApartmentById(@PathVariable("id") Long id) {
        Apartment existed = apartmentService.getApartmentById(id);
        Map<String, Object> response = new HashMap<>();
        if (existed != null) {
            response.put("data", existed);
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("message", "Không tìm thấy căn hộ này");
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
