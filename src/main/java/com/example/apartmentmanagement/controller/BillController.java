package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.BillDTO;
import com.example.apartmentmanagement.dto.BillRequestDTO;
import com.example.apartmentmanagement.entities.Bill;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.service.BillService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bill")
public class BillController {
    @Autowired
    private BillService billService;

    /**
     * (Staff) Xem danh sach bill trong khoang thoi gian cu the
     *
     * @param month
     * @param year
     * @return
     */
    @GetMapping("/getAll")
    public List<BillDTO> getBill(@RequestParam int month, @RequestParam int year) {
        return billService.getAllBillsWithinSpecTime(month, year);
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
    public List<BillDTO> getBillList(@RequestParam int month, @RequestParam int year, HttpSession session) {
        Object sessionUser = session.getAttribute("user");
        if (sessionUser == null) {
            throw new RuntimeException("User not found in session");
        }
        User user = (User) sessionUser;
        return billService.viewBillList(month, year, user.getUserId());
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
        if (result.equals("success")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }
}
