package com.example.apartmentmanagement.service.serviceImpl;

import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.Resident;
import com.example.apartmentmanagement.repository.ApartmentRepository;
import com.example.apartmentmanagement.repository.ResidentRepository;
import com.example.apartmentmanagement.service.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResidentServiceImpl implements ResidentService {

    @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Override
    public String addResident(Resident resident, Long id) {
        Apartment apartment = apartmentRepository.findById(id).orElse(null);
        if (apartment == null) {
            return "Cannot found apartment";
        }
        int total = residentRepository.countResidentsByApartmentId(apartment.getId());
        int allowedMember = apartment.getTotalNumber();
        if (total<=allowedMember) {
            resident.setApartment(apartment);
            residentRepository.save(resident);
            return "Add successfully";
        } else {
            return "Cannot add more people";
        }
    }
}
