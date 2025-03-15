package com.example.apartmentmanagement.entities;

/***
 * Entity post_images: Hình ảnh của bài đăng
 */

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "post_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postImagesId;

    private String postImagesUrl;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
