package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.ApartmentDTO;
import com.example.apartmentmanagement.entities.Apartment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApartmentService {

    String addApartment(Apartment apartment);

    List<ApartmentDTO> showApartment();

    Apartment getApartmentById (Long id);

    List<ApartmentDTO> getApartmentByName (String name);

    List<ApartmentDTO> totalUnrentedApartment ();

    List<ApartmentDTO> getOwnApartment (Long userId);
}
