package com.example.apartmentmanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "facility_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityImagesId;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Facility facility;
}
