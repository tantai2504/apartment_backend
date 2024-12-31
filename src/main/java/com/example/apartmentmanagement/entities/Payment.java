package com.example.apartmentmanagement.entities;

/***
 * Entity payment: luu tru thanh toan cua mot user
 */

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String paymentInfo;

    private Date paymentDate;

    @ManyToOne
    @JoinColumn(name = "user_Id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
    private Bill bill;
}
