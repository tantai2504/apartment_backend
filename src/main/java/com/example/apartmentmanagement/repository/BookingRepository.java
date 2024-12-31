package com.example.apartmentmanagement.repository;

import com.example.apartmentmanagement.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
