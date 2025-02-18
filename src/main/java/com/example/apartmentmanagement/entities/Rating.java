package com.example.apartmentmanagement.entities;

/***
 * Entity rating: danh gia can ho (thuong se la danh gia sau 1 thoi gian thue)
 */

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "rating")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;

    private String rate;

    private LocalDateTime rateDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
}
