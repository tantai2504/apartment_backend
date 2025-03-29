package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.BillResponseDTO;
import com.example.apartmentmanagement.dto.BillRequestDTO;
import com.example.apartmentmanagement.entities.Bill;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.BillRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.BillService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bill")
public class BillController {
    @Autowired
    private BillService billService;

    @Autowired
    private UserRepository userRepository;

    /**
     * (Staff) Xem danh sach bill cua user bat ky trong khoang thoi gian cu the
     *
     * @param month
     * @param year
     * @return
     */
    @GetMapping("/getAll/{userId}")
    public ResponseEntity<Object> getBill(@RequestParam int month, @RequestParam int year,
                                          @PathVariable Long userId) {
        List<BillResponseDTO> billResponseDTOS = billService.getAllBillsWithinSpecTime(userId, month, year);
        Map<String, Object> response = new HashMap<>();
        if (billResponseDTOS.isEmpty()) {
            response.put("message", "Chưa thanh toán hoá đơn nào");
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("data", billResponseDTOS);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    /**
     * (User) Xem danh sach bill cua ban than trong khoang thoi gian cu the
     *
     * @param month
     * @param year
     * @param userId
     * @return
     */
    @GetMapping("/view_bill_list/{userId}")
    public ResponseEntity<Object> getBillList(@RequestParam int month, @RequestParam int year, @PathVariable Long userId) {
        User user = userRepository.findById(userId).get();
        List<BillResponseDTO> billResponseDTOS = billService.viewBillList(month, year, user.getUserId());
        Map<String, Object> response = new HashMap<>();
        if (billResponseDTOS.isEmpty()) {
            response.put("message", "Chưa thanh toán hoá đơn nào");
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("data", billResponseDTOS);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    /**
     * (Staff) Tao hoa don cho user
     *
     * @param request
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<Object> createBill(@RequestBody BillRequestDTO request) {
        String result = billService.addBill(request.getBillContent(),
                request.getUserName(),
                request.getElectricCons(),
                request.getWaterCons(),
                request.getOthers());
        Map<String, Object> response = new HashMap<>();
        if (result.equals("success")) {
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", request);
            response.put("message", "Tạo hoá đơn thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Khởi tạo thất bại");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/get_bill_info/{billId}")
    public ResponseEntity<Object> getBillInfo(@RequestParam int month, @RequestParam int year, @PathVariable Long billId) {
        BillResponseDTO billResponseDTO = billService.getBillById(billId);
        Map<String, Object> response = new HashMap<>();
        if (billResponseDTO == null) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Không tìm thấy hoá đơn này");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            response.put("status", HttpStatus.OK.value());
            response.put("data", billResponseDTO);
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/update/{billid}")
    public ResponseEntity<Object> updateBill(@PathVariable Long billid, @RequestBody BillRequestDTO request) {
        Map<String, Object> response = new HashMap<>();
        try {
            BillResponseDTO billResponseDTO = billService.updateBill(billid, request);
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", billResponseDTO);
            response.put("message", "Update bill thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    /**
     * (User) xoá hoá đơn đã thanh toán
     *
     * @param billId
     * @return
     */
    @DeleteMapping("/delete/{billId}")
    public ResponseEntity<Object> deleteBill(@PathVariable Long billId) {
        Map<String, Object> response = new HashMap<>();
        try {
            billService.deleteBill(billId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}