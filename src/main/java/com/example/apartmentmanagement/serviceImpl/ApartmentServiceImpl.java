package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.ApartmentDTO;
import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.ApartmentRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.ApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApartmentServiceImpl implements ApartmentService{
    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String addApartment(Apartment apartment) {
        apartment.setStatus("unrented");
        apartmentRepository.save(apartment);
        return "Add successfully";
    }

    @Override
    public List<ApartmentDTO> showApartment() {
        return apartmentRepository.findAll().stream().map(apartment -> new ApartmentDTO(
                apartment.getApartmentId(),
                apartment.getApartmentName(),
                apartment.getHouseholder(),
                apartment.getTotalNumber(),
                apartment.getStatus(),
                apartment.getAptImgUrl(),
                apartment.getNumberOfBedrooms(),
                apartment.getNumberOfBathrooms(),
                apartment.getNote(),
                apartment.getUsers().stream().map(User::getUserName).toList()
        )).toList();
    }

    @Override
    public Apartment getApartmentById(Long id) {
        Apartment apartment = apartmentRepository.findById(id).orElse(null);
        return apartment;
    }

    @Override
    public List<ApartmentDTO> getApartmentByName(String name) {
        return apartmentRepository.findApartmentByApartmentNameContaining(name).stream()
                .map(apartment -> new ApartmentDTO(
                        apartment.getApartmentId(),
                        apartment.getApartmentName(),
                        apartment.getHouseholder(),
                        apartment.getTotalNumber(),
                        apartment.getStatus(),
                        apartment.getAptImgUrl(),
                        apartment.getNumberOfBedrooms(),
                        apartment.getNumberOfBathrooms(),
                        apartment.getNote(),
                        apartment.getUsers().stream().map(User::getUserName).toList()
                ))
                .toList();
    }


    @Override
    public List<ApartmentDTO> totalUnrentedApartment() {
        return apartmentRepository.findAll().stream().
                filter(apartment -> apartment.getStatus().equals("unrented")).
                map(apartment -> new ApartmentDTO(
                apartment.getApartmentId(),
                apartment.getApartmentName(),
                apartment.getHouseholder(),
                apartment.getTotalNumber(),
                apartment.getStatus(),
                apartment.getAptImgUrl(),
                apartment.getNumberOfBedrooms(),
                apartment.getNumberOfBathrooms(),
                apartment.getNote(),
                apartment.getUsers().stream().map(User::getUserName).toList()
        )).toList();
    }

    @Override
    public List<ApartmentDTO> getOwnApartment(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user.getRole().equals("admin")) {
            return apartmentRepository.findAll().stream().
                    filter(apartment -> apartment.getStatus().equals("unrented")).
                    map(apartment -> new ApartmentDTO(
                            apartment.getApartmentId(),
                            apartment.getApartmentName(),
                            apartment.getHouseholder(),
                            apartment.getTotalNumber(),
                            apartment.getStatus(),
                            apartment.getAptImgUrl(),
                            apartment.getNumberOfBedrooms(),
                            apartment.getNumberOfBathrooms(),
                            apartment.getNote(),
                            apartment.getUsers().stream().map(User::getUserName).toList()
                    )).toList();
        } else {
            return null;
        }
    }
}
