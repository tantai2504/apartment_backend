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

    //CCCD
    private String idNumber;

    @Nationalized
    private String job;

    @Nationalized
    private String role;

    @OneToOne
    @JoinColumn(name = "verification_form_id", nullable = true, unique = true)
    private VerificationForm verificationForm;

    @ManyToMany
    @JoinTable(
            name = "user_apartment",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "apartment_id")
    )
    private List<Apartment> apartments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deposit> deposits;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Card card;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bill> bills;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports;
}
