package com.example.apartmentmanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "facility")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityId;

    @Lob
    @Nationalized
    private String facilityContent;

    private LocalDateTime facilityPostingDate;

    // verified & unverified
    private String verifiedCheck;

    private LocalDateTime verifiedCheckDate;

    // trong trường hợp admin reject post thì sẽ nhập reason gửi về
    @Lob
    @Nationalized
    private String reason;

    private Long verifiedUserId;

    @ManyToOne
    @JoinColumn(name = "user_Id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacilityImages> facilityImages;
}
