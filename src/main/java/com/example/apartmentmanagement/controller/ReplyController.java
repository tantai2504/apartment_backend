package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.ReplyDTO;
import com.example.apartmentmanagement.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/replies")
public class ReplyController {
    @Autowired
    private ReplyService replyService;

    @GetMapping("/report/{reportId}")
    public ResponseEntity<Object> getRepliesByReportId(@PathVariable Long reportId) {
        List<ReplyDTO> dtos = replyService.getRepliesByReportId(reportId);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getReplyById(@PathVariable Long id) {
        ReplyDTO result = replyService.getReplyById(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ReplyDTO> createReply(@RequestBody ReplyDTO replyDto) {
        ReplyDTO result = replyService.createReply(replyDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReplyDTO> updateReply(@PathVariable Long id, @RequestBody ReplyDTO newReplyData) {
        ReplyDTO result = replyService.updateReply(id, newReplyData);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long id) {
        replyService.deleteReply(id);
        return ResponseEntity.noContent().build();
    }
}
