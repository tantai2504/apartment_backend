package com.example.apartmentmanagement.entities;

import com.example.apartmentmanagement.enums.BankEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recoin")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReCoin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reCoinId;

    @ManyToOne
    @JoinColumn(name = "user_Id")
    private User user;

    private String bankNumber;

    @Nationalized
    private String bankName;
    private String bankPin;
    private String accountName;
    private Float amount;
    private String imgQR;
    private String imgBill;
    private String status;
    private String content;
    private LocalDateTime dateTime;
    private String reason;
    private LocalDateTime dateAcceptReject;
    private LocalDateTime dateComplete;

    public ReCoin(Long reCoinId, User user, Float amount, String bankNumber, String bankName, String bankPin, String accountName, String imgQR, String status) {
        this.reCoinId = reCoinId;
        this.user = user;
        this.amount = amount;
        this.bankNumber = bankNumber;
        this.bankName = bankName;
        this.bankPin = bankPin;
        this.accountName = accountName;
        this.imgQR = imgQR;
        this.status = status;
    }
}
