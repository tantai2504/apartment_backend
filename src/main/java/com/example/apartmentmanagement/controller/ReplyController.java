package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.ReplyDTO;
import com.example.apartmentmanagement.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/replies")
public class ReplyController {
    @Autowired
    private ReplyService replyService;

    @GetMapping("/report/{reportId}")
    public List<ReplyDTO> getRepliesByReportId(@PathVariable Long reportId) {
        return replyService.getRepliesByReportId(reportId);
    }

    @GetMapping("/{id}")
    public ReplyDTO getReplyById(@PathVariable Long id) {
        return replyService.getReplyById(id);
    }

    @PostMapping
    public ReplyDTO createReply(@RequestBody ReplyDTO replyDto) {
        return replyService.createReply(replyDto);
    }

    @PutMapping("/{id}")
    public ReplyDTO updateReply(@PathVariable Long id, @RequestBody ReplyDTO newReplyData) {
        return replyService.updateReply(id, newReplyData);
    }

    @DeleteMapping("/{id}")
    public void deleteReply(@PathVariable Long id) {
        replyService.deleteReply(id);
    }
}
