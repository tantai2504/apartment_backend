package com.example.apartmentmanagement.entities;

/***
 * Entity facility: dich vu, tien ich tu ben thu ba
 */

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "facility")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityId;

    private String facilityType;

    /**
     * @param issuanceDate: ngay ky hop dong
     */
    private Date startDate;

    /**
     * @param expirationDate: ngay het han
     */
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "user_Id", nullable = false)
    private User user;
}
