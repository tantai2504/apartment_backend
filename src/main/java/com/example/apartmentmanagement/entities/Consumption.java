package com.example.apartmentmanagement.entities;

/***
 * Entity consumption: luu du lieu tieu thu dien nuoc (kWh) hang thang
 */

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;

@Entity
@Table(name = "consumption")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Consumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long consumptionId;

    private LocalDate consumptionDate;

    private float waterConsumption;

    private float lastMonthWaterConsumption;

    private boolean isBillCreated;

    private Long uploadConsumptionUserId;

    @OneToOne(mappedBy = "consumption", cascade = CascadeType.ALL, orphanRemoval = true)
    private Bill bill;

    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

}
