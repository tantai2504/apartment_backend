package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.ReplyDTO;
import com.example.apartmentmanagement.entities.Reply;
import com.example.apartmentmanagement.entities.Report;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.ReplyRepository;
import com.example.apartmentmanagement.repository.ReportRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReplyServiceImpl implements ReplyService {
    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    // Chuyển từ entity Reply -> ReplyDTO
    private ReplyDTO convertToDto(Reply reply) {
        return new ReplyDTO(
                reply.getReplyId(),
                reply.getReplyContent(),
                reply.getReplyDate(),
                reply.getUser().getUserId(),
                reply.getReport().getReportId()
        );
    }

    @Override
    public List<ReplyDTO> getRepliesByReportId(Long reportId) {
        List<ReplyDTO> replyList = replyRepository.findByReport_ReportId(reportId)
                .stream()
                .map(reply -> new ReplyDTO(
                        reply.getReplyId(),
                        reply.getReplyContent(),
                        reply.getReplyDate(),
                        reply.getUser().getUserId(),
                        reply.getReport().getReportId()
                ))
                .collect(Collectors.toList());

        // In ra dữ liệu để kiểm tra
        System.out.println("Reply DTO List: " + replyList);

        return replyList;
    }

    @Override
    public ReplyDTO getReplyById(Long id) {
        return replyRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    @Override
    public ReplyDTO createReply(ReplyDTO replyDto) {
        User user = userRepository.findById(replyDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Report report = reportRepository.findById(replyDto.getReportId())
                .orElseThrow(() -> new RuntimeException("Report not found"));

        Reply reply = new Reply();
        reply.setReplyContent(replyDto.getReplyContent());
        reply.setReplyDate(LocalDateTime.now());
        reply.setUser(user);
        reply.setReport(report);

        return convertToDto(replyRepository.save(reply));
    }

    @Override
    public ReplyDTO updateReply(Long id, ReplyDTO newReplyData) {
        Optional<Reply> replyOptional = replyRepository.findById(id);
        if (replyOptional.isPresent()) {
            Reply reply = replyOptional.get();
            reply.setReplyContent(newReplyData.getReplyContent());
            reply.setReplyDate(LocalDateTime.now());
            return convertToDto(replyRepository.save(reply));
        }
        return null;
    }

    @Override
    public void deleteReply(Long id) {
        replyRepository.deleteById(id);
    }
}
