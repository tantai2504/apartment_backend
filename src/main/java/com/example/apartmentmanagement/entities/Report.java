package com.example.apartmentmanagement.entities;

/***
 * Entity report: bao cao nhung van de o can ho
 */

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Nationalized
    private String reportContent;

    private LocalDateTime reportDate;

    /**
     * @param reportCheck: kiem tra report da duoc phan hoi hay chua
     */
    private boolean reportCheck;

    @ManyToOne
    @JoinColumn(name = "user_Id", nullable = false)
    private User user;

}
