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
import java.time.LocalDateTime;

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

    private LocalDateTime consumptionDate;

    private String waterConsumption;

    private String electricConsumption;

    @ManyToOne
    @JoinColumn(name = "user_Id", nullable = false)
    private User user;
}
