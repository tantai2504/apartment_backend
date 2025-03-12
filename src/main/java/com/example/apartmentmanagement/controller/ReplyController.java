package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.ReplyDTO;
import com.example.apartmentmanagement.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/replies")
public class ReplyController {
    @Autowired
    private ReplyService replyService;

    @GetMapping("/report/{reportId}")
    public ResponseEntity<Object> getRepliesByReportId(@PathVariable Long reportId) {
        List<ReplyDTO> dtos = replyService.getRepliesByReportId(reportId);
        Map<String, Object> response = new HashMap<>();
        if (dtos.isEmpty()) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Chưa có phản hồi nào");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("status", HttpStatus.OK.value());
        response.put("data", dtos);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getReplyById(@PathVariable Long id) {
        ReplyDTO dtos = replyService.getReplyById(id);
        Map<String, Object> response = new HashMap<>();
        if (dtos == null) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Không tìm thấy phản hồi này");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("status", HttpStatus.OK.value());
        response.put("data", dtos);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Object> createReply(@RequestBody ReplyDTO replyDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            ReplyDTO result = replyService.createReply(replyDto);
            response.put("status", HttpStatus.CREATED.value());
            response.put("message", "Phản hồi thành công");
            response.put("data", result);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Object> updateReply(@PathVariable Long id, @RequestBody ReplyDTO newReplyData) {
        ReplyDTO result = replyService.updateReply(id, newReplyData);
        Map<String, Object> response = new HashMap<>();
        if (result == null) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Cập nhật thất bại");
        }
        response.put("status", HttpStatus.CREATED.value());
        response.put("data", result);
        response.put("message", "Cập nhật thành công");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteReply(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        replyService.deleteReply(id);
        return ResponseEntity.noContent().build();
    }
}
