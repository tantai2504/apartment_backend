package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.*;
import com.example.apartmentmanagement.entities.ReCoin;
import com.example.apartmentmanagement.service.BankAccountService;
import com.example.apartmentmanagement.service.ReCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recoin")
public class ReCoinController {
    @Autowired
    private ReCoinService reCoinService;

    @PostMapping("/add")
    public ResponseEntity<Object> addRequestReCoin(
            @RequestBody ReCoinRequestDTO reCoinRequestDTO) {
        Map<String, Object> response = new HashMap<>();
        try{
            ReCoinResponseDTO res = reCoinService.addRecoin(reCoinRequestDTO);
            response.put("message", "Tạo yêu cầu rút tiền thành công");
            response.put("data", res);
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<Object> getAll() {
        List<ReCoinResponseDTO> recoins = reCoinService.listAllReCoin();
        Map<String, Object> response = new HashMap<>();
        if (recoins.isEmpty()) {
            response.put("message", "Không có yêu cầu rút tiền nào");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("data", recoins);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getByUserId/{userId}")
    public ResponseEntity<Object> getByUserId(@PathVariable Long userId) {
        List<ReCoinResponseDTO> recoins = reCoinService.listReCoinByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        if (recoins.isEmpty()) {
            response.put("message", "Không có yêu cầu rút tiền nào");
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("data", recoins);
        response.put("status", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accept")
    public ResponseEntity<Object> accept(@RequestBody ReCoinUpdateRequestDTO reCoinUpdateRequestDTO) {
        Map<String, Object> response = new HashMap<>();
        try{
            reCoinService.acceptReCoin(reCoinUpdateRequestDTO);
            response.put("message", "Đã xác nhận chuyển tiền");
//            response.put("data", res);
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<Object> reject(@RequestBody ReCoinUpdateRequestDTO reCoinUpdateRequestDTO) {
        Map<String, Object> response = new HashMap<>();
        try{
            reCoinService.rejectReCoin(reCoinUpdateRequestDTO);
            response.put("message", "Đã từ chối yêu cầu rút tiền");
//            response.put("data", res);
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/acceptReceived")
    public ResponseEntity<Object> acceptReceived(@RequestBody ReCoinUpdateRequestDTO reCoinUpdateRequestDTO) {
        Map<String, Object> response = new HashMap<>();
        try{
            reCoinService.acceptReceivedReCoin(reCoinUpdateRequestDTO);
            response.put("message", "Xác nhận rút tiền thành công");
//            response.put("data", res);
            response.put("status", HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
