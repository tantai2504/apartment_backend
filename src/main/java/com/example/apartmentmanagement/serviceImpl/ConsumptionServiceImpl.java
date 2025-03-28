package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.ApartmentResponseInUserDTO;
import com.example.apartmentmanagement.dto.ConsumptionResponseDTO;
import com.example.apartmentmanagement.dto.UserRequestDTO;
import com.example.apartmentmanagement.dto.UserResponseDTO;
import com.example.apartmentmanagement.entities.Consumption;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.ConsumptionRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.ConsumptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsumptionServiceImpl implements ConsumptionService {
    @Autowired
    private ConsumptionRepository consumptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ConsumptionResponseDTO> getAllConsumptionsByUser(int month, int year, Long userId) {
        User user = userRepository.findById(userId).get();
        List<Consumption> consumptions = user.getConsumptions();
        return consumptions.stream().filter(consumption -> consumption.getConsumptionDate().getMonthValue() == month && consumption.getConsumptionDate().getYear() == year)
                .map(consumption -> new ConsumptionResponseDTO(
                consumption.getConsumptionId(),
                consumption.getConsumptionDate(),
                consumption.getWaterConsumption(),
                consumption.getElectricConsumption()
        )).collect(Collectors.toList());
    }

    @Override
    public List<ConsumptionResponseDTO> viewAllConsumption(int month, int year) {
        List<Consumption> consumptions = consumptionRepository.findAll();
        return consumptions.stream().filter(consumption -> consumption.getConsumptionDate().getMonthValue() == month && consumption.getConsumptionDate().getYear() == year)
                .map(consumption -> new ConsumptionResponseDTO(
                        consumption.getConsumptionId(),
                        consumption.getConsumptionDate(),
                        consumption.getWaterConsumption(),
                        consumption.getElectricConsumption(),
                        convertToUserResponseDTO(consumption.getUser())
                )).collect(Collectors.toList());
    }

    private UserResponseDTO convertToUserResponseDTO(User user) {
        if (user == null) return null;
        return new UserResponseDTO(
                user.getUserId(),
                user.getUserName(),
                user.getFullName(),
                user.getEmail(),
                user.getDescription(),
                user.getPhone(),
                user.getUserImgUrl(),
                user.getAge(),
                user.getBirthday(),
                user.getIdNumber(),
                user.getJob(),
                user.getApartments().stream()
                        .map(apartment -> new ApartmentResponseInUserDTO(
                                apartment.getApartmentId(),
                                apartment.getApartmentName(),
                                apartment.getHouseholder(),
                                apartment.getTotalNumber(),
                                apartment.getStatus(),
                                apartment.getAptImgUrl(),
                                apartment.getNumberOfBedrooms(),
                                apartment.getNumberOfBathrooms(),
                                apartment.getNote()
                        )).collect(Collectors.toList()),
                user.getRole()
        );
    }
}
