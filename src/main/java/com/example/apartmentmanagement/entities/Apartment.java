package com.example.apartmentmanagement.entities;

import com.example.apartmentmanagement.validate.ValidPositiveNumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    private Long id;

    @NotBlank(message = "Apartment name must not be null")
    @Column(name = "apartment_name", nullable = false)
    private String apartmentName;

    @Column(name = "householder")
    private String householder;

    @Column(name = "total_member")
    private int totalNumber;

    @Column(name = "status")
    private String status;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resident> residents;

}
