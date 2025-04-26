package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.ApartmentResponseDTO;
import com.example.apartmentmanagement.dto.UserResponseDTO;
import com.example.apartmentmanagement.entities.Apartment;

import java.util.List;

public interface ApartmentService {
    //list danh sach can ho cua owner
    List<ApartmentResponseDTO> getOwnApartments(Long userId);

    List<ApartmentResponseDTO> showApartment();

    Apartment getApartmentById (Long id);

    List<ApartmentResponseDTO> getApartmentByName (String name);

    List<ApartmentResponseDTO> totalUnrentedApartment ();

    List<ApartmentResponseDTO> getOwnUnrentedApartment (Long userId);

    List<ApartmentResponseDTO> getOwnRentedApartment (Long userId);

    List<ApartmentResponseDTO> getOwnApartmentRented (Long userId);

    List<ApartmentResponseDTO> findApartmentsWithoutHouseholder();

    ApartmentResponseDTO updateApartment(Long apartmentId, ApartmentResponseDTO apartmentDTO);

    ApartmentResponseDTO createApartment(ApartmentResponseDTO apartmentDTO);

    void deleteApartment(Long apartmentId);

    List<UserResponseDTO> getRentorByApartment(String apartmentName);

    List<ApartmentResponseDTO> getApartmentByRenter(Long userId);
}
