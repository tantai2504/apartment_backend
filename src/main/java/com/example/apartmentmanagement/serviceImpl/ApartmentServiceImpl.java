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
    public String addResidentIntoApartment(Apartment apartment, Long userId, MultipartFile imageFile) {
        List<Apartment> apartmentList = apartmentRepository.findAll();

        for (Apartment a : apartmentList) {
            if (apartment.getApartmentName().equals(a.getApartmentName())) {
                return "Existed apartment";
            }
        }

        if (apartment.getTotalNumber() == 0 &&
                (apartment.getHouseholder() == null || apartment.getHouseholder().isEmpty())) {
            apartment.setHouseholder("");
            apartment.setStatus("unrented");
        } else {
            apartment.setStatus("rented");
        }

        String imgUrl = imageUploadService.uploadImage(imageFile);
        if (imgUrl.equals("image-url")) {
            apartment.setAptImgUrl("");
        } else {
            apartment.setAptImgUrl(imgUrl);
        }
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }
        List<User> userList = new ArrayList<>();
        userList.add(user);
        apartment.setUsers(userList);
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
    public List<ApartmentDTO> showApartment() {
        return apartmentRepository.findAll().stream().map(apartment -> new ApartmentDTO(
                apartment.getApartmentId(),
                apartment.getApartmentName(),
                apartment.getHouseholder(),
                apartment.getTotalNumber(),
                apartment.getStatus(),
                apartment.getAptImgUrl(),
                apartment.getUsers().stream().map(User::getUserName).toList()
        )).toList();
    }

    @Override
    public Apartment getApartmentById(Long id) {
        Apartment apartment = apartmentRepository.findById(id).orElse(null);
        return apartment;
    }

    @Override
    public void updateApartment(Apartment existedApartment, Apartment apartment, MultipartFile imageFile) {
        existedApartment.setApartmentName(apartment.getApartmentName());
        existedApartment.setHouseholder(apartment.getHouseholder());
        existedApartment.setTotalNumber(apartment.getTotalNumber());
        if (imageFile != null) {
            String imgUrl = imageUploadService.uploadImage(imageFile);
            apartment.setAptImgUrl(imgUrl);
        } else {
            apartment.setAptImgUrl(" ");
        }
        apartmentRepository.save(existedApartment);
    }

    @Override
    public void deleteApartment(Long id) {
        apartmentRepository.deleteById(id);
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
                apartment.getUsers().stream().map(User::getUserName).toList()
        )).toList();
    }
}
