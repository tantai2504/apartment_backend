package com.example.apartmentmanagement.entities;

import jakarta.persistence.*;
import lombok.*;

/***
 * Entity deposit: luu du lieu dat coc
 */

@Entity
@Table(name = "deposit")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Deposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long depositId;

    @ManyToOne
    @JoinColumn(name = "user_Id")
    private User user;

    private String status;

    private float price;

    @ManyToOne
    @JoinColumn(name = "apartment_id", referencedColumnName = "apartment_id")
    private Apartment apartment;

    @OneToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "paymentId")
    private Payment payment;
}
