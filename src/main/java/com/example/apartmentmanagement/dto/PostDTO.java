package com.example.apartmentmanagement.dto;

import com.example.apartmentmanagement.entities.PostImages;
import com.example.apartmentmanagement.entities.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long postId;

    private String title;

    private String content;

    private boolean depositCheck;

    private ApartmentDTO apartmentName;

    private float price;

    private String postType;

    private LocalDateTime postDate;

    private String userName;

    private List<String> postImages;
}
