package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.ApartmentResponseDTO;
import com.example.apartmentmanagement.entities.Apartment;

import java.util.List;

public interface ApartmentService {

    String addApartment(Apartment apartment);

    List<ApartmentResponseDTO> showApartment();

    Apartment getApartmentById (Long id);

    List<ApartmentResponseDTO> getApartmentByName (String name);

    List<ApartmentResponseDTO> totalUnrentedApartment ();

    List<ApartmentResponseDTO> getOwnApartment (Long userId);
}
