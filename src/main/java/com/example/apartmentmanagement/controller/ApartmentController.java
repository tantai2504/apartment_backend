package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.ApartmentResponseDTO;
import com.example.apartmentmanagement.dto.ConsumptionResponseDTO;
import com.example.apartmentmanagement.dto.UserResponseDTO;
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

    @GetMapping("/getAll/no-householder")
    public ResponseEntity<Object> showAllApartmentsWithoutHouseholder() {
        List<ApartmentResponseDTO> apartments = apartmentService.findApartmentsWithoutHouseholder();
        Map<String, Object> response = new HashMap<>();
        if (apartments.isEmpty()) {
            response.put("message", "Tất cả các căn hộ đều đã có chủ hộ");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("data", apartments);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get_own_apartment")
    public ResponseEntity<Object> showOwnApartment(@RequestParam Long userId) {
        List<ApartmentResponseDTO> apartments = apartmentService.getOwnApartments(userId);
        Map<String, Object> response = new HashMap<>();
        if (apartments.isEmpty()) {
            response.put("message", "Không tìm thấy căn hộ nào");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("data", apartments);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    //lay danh sach can ho unrented cua owner
    @GetMapping("/get_own_unrented_apartment")
    public ResponseEntity<Object> getOwnUnrentedApartment(@RequestParam Long userId) {
        List<ApartmentResponseDTO> apartments = apartmentService.getOwnUnrentedApartment(userId);
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


    //lay can ho da cho thue
    @GetMapping("/getOwnerApartmentRented/{userId}")
    public ResponseEntity<Object> getOwnApartmentRented(@PathVariable Long userId) {
        List<ApartmentResponseDTO> apartments = apartmentService.getOwnApartmentRented(userId);

        Map<String, Object> response = new HashMap<>();
        if (apartments.isEmpty()) {
            response.put("message", "Chưa cho thuê căn hộ nào");
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

    @PutMapping("/update/{apartmentId}")
    public ResponseEntity<Object> updateApartment(
            @PathVariable Long apartmentId,
            @RequestBody ApartmentResponseDTO apartmentDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            ApartmentResponseDTO updatedApartment = apartmentService.updateApartment(apartmentId, apartmentDTO);
            response.put("status", HttpStatus.OK.value());
            response.put("data", updatedApartment);
            response.put("message", "Cập nhật căn hộ thành công");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createApartment(@RequestBody ApartmentResponseDTO apartmentDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            ApartmentResponseDTO createdApartment = apartmentService.createApartment(apartmentDTO);
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", createdApartment);
            response.put("message", "Tạo căn hộ thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/delete/{apartmentId}")
    public ResponseEntity<Object> deleteApartment(@PathVariable Long apartmentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            apartmentService.deleteApartment(apartmentId);
            response.put("status", HttpStatus.NO_CONTENT.value());
            response.put("message", "Xóa căn hộ thành công");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/get_rentor")
    public ResponseEntity<Object> getRentor(@RequestBody String apartmentName) {
        List<UserResponseDTO> userResponseDTOS = apartmentService.getRentorByApartment(apartmentName);
        Map<String, Object> response = new HashMap<>();
        if (userResponseDTOS != null) {
            response.put("data", userResponseDTOS);
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("message", "Chưa có người thuê");
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
