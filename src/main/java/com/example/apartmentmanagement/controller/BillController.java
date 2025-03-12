package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.BillDTO;
import com.example.apartmentmanagement.dto.BillRequestDTO;
import com.example.apartmentmanagement.entities.Bill;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.BillRepository;
import com.example.apartmentmanagement.service.BillService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bill")
public class BillController {
    @Autowired
    private BillService billService;
    @Autowired
    private BillRepository billRepository;

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
        List<BillDTO> billDTOS = billService.getAllBillsWithinSpecTime(userId, month, year);
        Map<String, Object> response = new HashMap<>();
        if (billDTOS.isEmpty()) {
            response.put("message", "Chưa thanh toán hoá đơn nào");
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("data", billDTOS);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    /**
     * (User) Xem danh sach bill cua ban than trong khoang thoi gian cu the
     *
     * @param month
     * @param year
     * @param session
     * @return
     */
    @GetMapping("/view_bill_list")
    public ResponseEntity<Object> getBillList(@RequestParam int month, @RequestParam int year, HttpSession session) {
        Object sessionUser = session.getAttribute("user");
        User user = (User) sessionUser;
        List<BillDTO> billDTOS = billService.viewBillList(month, year, user.getUserId());
        Map<String, Object> response = new HashMap<>();
        if (billDTOS.isEmpty()) {
            response.put("message", "Chưa thanh toán hoá đơn nào");
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("data", billDTOS);
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

    @GetMapping("/")

//    @PutMapping("/update/{billid}")
//    public ResponseEntity<Object> updateBill(@PathVariable Long billid, @RequestBody BillRequestDTO request) {
//
//    }

    /**
     * (User) xoá hoá đơn đã thanh toán
     *
     * @param billId
     * @param session
     * @return
     */
    @DeleteMapping("/delete/{billId}")
    public ResponseEntity<Object> deleteBill(@PathVariable Long billId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        try {
            Bill bill = billRepository.findById(billId).get();
            if (!bill.getUser().getUserId().equals(user.getUserId())) {
                response.put("message", "Không có quyền xoá hoá đơn này");
                response.put("status", HttpStatus.FORBIDDEN.value());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            billService.deleteBill(billId);
            response.put("status", HttpStatus.NO_CONTENT.value());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
