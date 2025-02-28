package com.example.apartmentmanagement.entities;

/***
 * Entity user: tai khoan co ban truy cap vao trang web
 */

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "user_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String userName;

    @Nationalized
    private String fullName;

    private String password;

    private String email;

    @Nationalized
    private String description;

    private String phone;

    private String userImgUrl;

    private String age;

    private LocalDate birthday;

    /**
     * @param idNumber: can cuoc cong dan
     */
    private String idNumber;

    @Nationalized
    private String job;

    /**
     * @param role: phan quyen dua tren param nay (resident, owner, )
     */
    @Nationalized
    private String role;

    @ManyToOne
    @JoinColumn(name = "apartment_id", referencedColumnName = "apartment_id")
    private Apartment apartment;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Notification notification;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Card card;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bill> bills;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Facility> facilities;
}
