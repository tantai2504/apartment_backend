package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.entities.Resident;


public interface ResidentService {

    String addResident (Resident resident, Long id);

    Resident getResidentById (Long id);

}
