package com.example.apartmentmanagement.dto;

import com.example.apartmentmanagement.entities.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReCoinResponseDTO {
    private Long reCoinId;
    private String bankNumber;
    private String bankName;
    private String bankPin;
    private String accountName;
    private Float amount;
    private String imgQR;
    private String imgBill;
    private String status;
    private Long userRequestId;
    private String content;
    private LocalDateTime dateTime;
    private String fullName;
    private LocalDateTime dateAcceptReject;
    private LocalDateTime dateComplete;

    public ReCoinResponseDTO(Float amount, String imgQR, Long userRequestId, String content) {
        this.amount = amount;
        this.imgQR = imgQR;
        this.userRequestId = userRequestId;
        this.content = content;
    }
}
