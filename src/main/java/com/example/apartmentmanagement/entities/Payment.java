package com.example.apartmentmanagement.entities;

/***
 * Entity payment: luu tru thanh toan cua mot user
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
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    /**
     * @param paymentCheck: flag kiem tra da duoc thanh toan hay chua
     */
    private boolean paymentCheck;

    @Nationalized
    private String paymentInfo;

    private LocalDateTime paymentDate;

    private String paymentType;

    private float price;

    @ManyToOne
    @JoinColumn(name = "user_Id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
    private Bill bill;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
    private Deposit deposit;
}
