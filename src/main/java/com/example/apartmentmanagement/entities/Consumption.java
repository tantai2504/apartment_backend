package com.example.apartmentmanagement.entities;

/***
 * Entity consumption: luu du lieu tieu thu hang thang
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

}
