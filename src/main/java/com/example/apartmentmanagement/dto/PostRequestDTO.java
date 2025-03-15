package com.example.apartmentmanagement.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDTO {

    private String title;

    private String content;

    private boolean depositCheck;

    private String postType;

    private String userName;
}
