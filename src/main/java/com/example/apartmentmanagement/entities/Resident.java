package com.example.apartmentmanagement.entities;

/***
 * Entity resident: thong tin co ban cua cu dan o mot can ho (user sau khi mua hoac thue can ho)
 */

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resident")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long residentId;

    @NotBlank(message = "Full name must not be null")
    private String fullName;

    @NotBlank(message = "Age must not be null")
    private String age;

    /**
     * @param idNumber: can cuoc cong dan
     */
    @NotBlank(message = "Id Number must not be null")
    private String idNumber;

    @NotBlank(message = "Phone must not be null")
    private String phone;

    private String job;

    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    private User user;
}
