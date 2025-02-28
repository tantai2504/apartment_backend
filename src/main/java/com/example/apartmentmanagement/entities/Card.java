package com.example.apartmentmanagement.entities;

/***
 * Entity card: the dung cho viec do xe, thang may
 */

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;

@Entity
@Table(name = "card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    @Nationalized
    private String cardBaseId;

    /**
     * @param issuanceDate: ngay mo the
     */
    private LocalDate issuanceDate;

    /**
     * @param expirationDate: ngay het han
     */
    private LocalDate expirationDate;

    @Nationalized
    private String cardStatus;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    private User user;
}
