package com.example.apartmentmanagement.entities;

/***
 * Entity apartment: info co ban cua can ho
 */

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "apartment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long apartmentId;

    private String apartmentName;

    private String householder;

    private int totalNumber;

    /**
     * @param status: trang thai cua can ho: rented (duoc thue, mua), unrented (chua duoc thue, mua)
     */
    private String status;

    private String aptImgUrl;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = true)
    private User user;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resident> residents;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bill> bills;

}
