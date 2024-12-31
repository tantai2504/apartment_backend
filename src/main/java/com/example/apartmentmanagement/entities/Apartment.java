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

    @NotBlank(message = "Apartment name must not be null")
    @Column(name = "apartment_name", nullable = false)
    private String apartmentName;

    @Column(name = "householder")
    private String householder;

    @Column(name = "total_member")
    private int totalNumber;

    /**
     * @param status: trang thai cua can ho: rented (duoc thue, mua), unrented (chua duoc thue, mua)
     */
    @Column(name = "status")
    private String status;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    private User user;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resident> residents;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bill> bills;

}
