package com.example.apartmentmanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contract_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractImageId;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "verification_id", nullable = false)
    private VerificationForm verificationForm;
}
