package com.example.apartmentmanagement.entities;

/***
 * Entity post: Bài đăng cho thuê hoặc bán căn hộ
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
import java.util.List;

@Entity
@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Nationalized
    private String title;

    @Lob
    @Nationalized
    private String content;

    private String depositCheck;

    @Nationalized
    private String postType;

    private LocalDateTime postDate;

    private Float price = 0.0f;

    private Float depositPrice = 0.0f;
    /**
     * depositUserId: id của nguòi đặt cọc
     */
    private Long depositUserId;

    @ManyToOne
    @JoinColumn(name = "user_Id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "paymentId")
    private Payment payment;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImages> postImages;

    @OneToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;
}
