package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.DepositRequestDTO;
import com.example.apartmentmanagement.dto.DepositResponseDTO;
import com.example.apartmentmanagement.repository.PostRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.DepositService;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/deposit")
public class DepositController {
    @Autowired
    private DepositService depositService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<Object> makeDeposit(@RequestBody DepositRequestDTO depositRequestDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            DepositResponseDTO dto = depositService.createDeposit(depositRequestDTO);
            response.put("status", HttpStatus.CREATED.value());
            response.put("message", "Khởi tạo thành công");
            return null;
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
