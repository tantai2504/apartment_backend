package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.repository.ApartmentRepository;
import com.example.apartmentmanagement.service.ApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApartmentServiceImpl implements ApartmentService{
    @Autowired
    private ApartmentRepository apartmentRepository;

    @Override
    public String addApartment(Apartment apartment) {

        List<Apartment> apartmentList = apartmentRepository.findAll();

        for (Apartment a: apartmentList) {
            if (apartment.getApartmentName() == a.getApartmentName()) {
                return "Existed apartment";
            }
        }
        if(apartment.getTotalNumber() == 0 && (apartment.getHouseholder() == null || apartment.getHouseholder() == "")){
            apartment.setHouseholder("");
            apartment.setStatus("unrented");
        } else {
            apartment.setStatus("rented");
        }
        apartmentRepository.save(apartment);
        return "Add successfully";
    }

    @Override
    public String checkApartmentExisted(Apartment apartment) {
        List<Apartment> apartmentList = apartmentRepository.findAll();
        for (Apartment a : apartmentList) {
            if (apartment.getApartmentName().equals(a.getApartmentName())) {
                return "Existed apartment";
            }
        }
        return "Not existed";
    }

    @Override
    public List<Apartment> showApartment() {
         return apartmentRepository.findAll();
    }

    @Override
    public Apartment getApartmentById(Long id) {
        Apartment apartment = apartmentRepository.findById(id).orElse(null);
        return apartment;
    }

    @Override
    public void updateApartment(Apartment existedApartment, Apartment apartment) {
        existedApartment.setApartmentName(apartment.getApartmentName());
        existedApartment.setHouseholder(apartment.getHouseholder());
        existedApartment.setTotalNumber(apartment.getTotalNumber());
        apartmentRepository.save(existedApartment);
    }

    @Override
    public void deleteApartment(Long id) {
        apartmentRepository.deleteById(id);
    }

    @Override
    public List<Apartment> totalUnrentedApartment() {
        List<Apartment> apartmentList = new ArrayList<>();
        List<Apartment> totalApartment = apartmentRepository.findAll();
        for (Apartment a: totalApartment) {
            if (a.getStatus().equals("unrented")) {
                apartmentList.add(a);
            }
        }
        return apartmentList;
    }
}
