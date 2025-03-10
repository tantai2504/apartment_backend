package com.example.apartmentmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDTO {
    private Long replyId;
    private String replyContent;
    private LocalDateTime replyDate;
    private Long userId;
    private Long reportId;
}
