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
import org.hibernate.annotations.Nationalized;

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

    @Nationalized
    private String apartmentName;

    @Nationalized
    private String householder;

    /**
     * @param totalNumber: Tong so thanh vien trong can ho
     */
    private int totalNumber;

    private int numberOfBedrooms;

    private int numberOfBathrooms;

    private String note;

    /**
     * @param status: trang thai cua can ho: rented (duoc thue), unrented (chua duoc thue)
     */
    @Nationalized
    private String status;

    private String aptImgUrl;

    @Nationalized
    private String direction;

    private String floor;

    private String area;

    @ManyToMany(mappedBy = "apartments")
    private List<User> users;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bill> bills;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Consumption> consumptions;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;
}
