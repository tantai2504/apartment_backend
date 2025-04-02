package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.ApartmentResponseDTO;
import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.service.ApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        List<ApartmentResponseDTO> apartments = apartmentService.showApartment();
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
        List<ApartmentResponseDTO> apartment = apartmentService.getApartmentByName(name);
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
        List<ApartmentResponseDTO> apartments = apartmentService.totalUnrentedApartment();
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

    @GetMapping("/get_own_apartment")
    public ResponseEntity<Object> getOwnApartment(@RequestParam Long userId) {
        List<ApartmentResponseDTO> apartments = apartmentService.getOwnApartment(userId);
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
