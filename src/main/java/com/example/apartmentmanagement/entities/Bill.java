package com.example.apartmentmanagement.entities;

/***
 * Entity bill: hoa don, chi phi
 */

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    private String billContent;

    private float electricBill;

    private float waterBill;

    private float others;

    private float total;

    private LocalDateTime billDate;

    @OneToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "paymentId", nullable = false)
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "user_Id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;
}
