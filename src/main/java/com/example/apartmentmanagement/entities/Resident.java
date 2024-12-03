package com.example.apartmentmanagement.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resident")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name must not be null")
    private String fullName;

    @NotBlank(message = "Age must not be null")
    private String age;

    @NotBlank(message = "Id Number must not be null")
    private String idNumber;

    @NotBlank(message = "Phone must not be null")
    private String phone;

    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;
}
