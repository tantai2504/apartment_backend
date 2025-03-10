package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.ReplyDTO;
import java.util.List;

public interface ReplyService {
    List<ReplyDTO> getRepliesByReportId(Long reportId);
    ReplyDTO getReplyById(Long id);
    ReplyDTO createReply(ReplyDTO replyDto);
    ReplyDTO updateReply(Long id, ReplyDTO newReplyData);
    void deleteReply(Long id);
}
