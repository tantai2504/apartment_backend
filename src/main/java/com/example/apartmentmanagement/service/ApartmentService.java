package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.entities.Apartment;

import java.util.List;

public interface ApartmentService {
    String addApartment (Apartment apartment);

    String checkApartmentExisted(Apartment apartment);

    List<Apartment> showApartment();

    Apartment getApartmentById (Long id);

    void updateApartment (Apartment existedApartment, Apartment apartment);

    void deleteApartment (Long id);

    List<Apartment> totalUnrentedApartment ();
}
