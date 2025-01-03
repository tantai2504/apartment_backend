package com.example.apartmentmanagement.entities;

/***
 * Entity user: tai khoan co ban truy cap vao trang web
 */

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String password;

    private String email;

    private String description;

    private String phone;

    private String userImgUrl;

    /**
     * @param role: phan quyen dua tren param nay (visitor, resident, admin, facilityOwner)
     */
    private String role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Resident resident;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Apartment apartment;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Booking booking;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Notification notification;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Card card;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bill> bills;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Facility> facilities;
}
