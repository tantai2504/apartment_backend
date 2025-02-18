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
    @Column(name = "apartment_id")
    private Long apartmentId;

    private String apartmentName;

    private String householder;

    /**
     * @param totalNumber: Tong so thanh vien trong can ho
     */
    private int totalNumber;

    /**
     * @param status: trang thai cua can ho: rented (duoc thue), unrented (chua duoc thue)
     */
    private String status;

    private String aptImgUrl;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<User> users;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bill> bills;

}
