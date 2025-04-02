package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.ApartmentResponseInUserDTO;
import com.example.apartmentmanagement.dto.ConsumptionResponseDTO;
import com.example.apartmentmanagement.dto.UserResponseDTO;
import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.Consumption;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.ApartmentRepository;
import com.example.apartmentmanagement.repository.ConsumptionRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.ConsumptionService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConsumptionServiceImpl implements ConsumptionService {
    @Autowired
    private ConsumptionRepository consumptionRepository;

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ConsumptionResponseDTO> getAllConsumptionsByUser(int month, int year, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Apartment> apartments = user.getApartments();
        if (apartments == null || apartments.isEmpty()) {
            throw new RuntimeException("No apartments found for this user");
        }

        List<Consumption> consumptions = new ArrayList<>();
        for (Apartment apartment : apartments) {
            List<Consumption> apartmentConsumptions = consumptionRepository.findByApartment(apartment);

            List<Consumption> filteredConsumptions = apartmentConsumptions.stream()
                    .filter(consumption ->
                            consumption.getConsumptionDate().getMonthValue() == month &&
                                    consumption.getConsumptionDate().getYear() == year)
                    .collect(Collectors.toList());

            consumptions.addAll(filteredConsumptions);
        }

        return consumptions.stream()
                .map(consumption -> new ConsumptionResponseDTO(
                        consumption.getConsumptionId(),
                        consumption.getConsumptionDate(),
                        consumption.getWaterConsumption(),
                        consumption.getLastMonthWaterConsumption(),
                        consumption.getApartment().getHouseholder(),
                        consumption.getApartment().getApartmentName(),
                        consumption.isBillCreated()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsumptionResponseDTO> getAll() {
        List<Consumption> consumptions = consumptionRepository.findAll();

        for (Consumption consumption : consumptions) {
            System.out.println(consumption.getApartment().getApartmentName());
        }

        return consumptions.stream()
                .map(consumption -> new ConsumptionResponseDTO(
                        consumption.getConsumptionId(),
                        consumption.getConsumptionDate(),
                        consumption.getLastMonthWaterConsumption(),
                        consumption.getWaterConsumption(),
                        consumption.getApartment().getHouseholder(),
                        consumption.getApartment().getApartmentName(),
                        consumption.isBillCreated()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsumptionResponseDTO> viewAllConsumption(int month, int year) {
        List<Apartment> apartments = apartmentRepository.findAll();
        List<Consumption> consumptions = new ArrayList<>();
        for (Apartment apartment : apartments) {
            List<Consumption> apartmentConsumptions = consumptionRepository.findByApartment(apartment);

            List<Consumption> filteredConsumptions = apartmentConsumptions.stream()
                    .filter(consumption ->
                            consumption.getConsumptionDate().getMonthValue() == month &&
                                    consumption.getConsumptionDate().getYear() == year)
                    .collect(Collectors.toList());

            consumptions.addAll(filteredConsumptions);
        }

        return consumptions.stream()
                .map(consumption -> new ConsumptionResponseDTO(
                        consumption.getConsumptionId(),
                        consumption.getConsumptionDate(),
                        consumption.getLastMonthWaterConsumption(),
                        consumption.getWaterConsumption(),
                        consumption.getApartment().getHouseholder(),
                        consumption.getApartment().getApartmentName(),
                        consumption.isBillCreated()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsumptionResponseDTO> processExcelFile(MultipartFile file, Long createdUserId) throws IOException {
        User verifiedUser = userRepository.findById(createdUserId).orElse(null);
        if (verifiedUser.getRole().equals("Admin") || verifiedUser.getRole().equals("Staff")) {

        }
        List<ConsumptionResponseDTO> responseDTOs = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            String firstSheetName = workbook.getSheetName(0);

            LocalDate currentDate = LocalDate.now();
            String expectedSheetName = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            if (!firstSheetName.equals(expectedSheetName)) {
                throw new IllegalArgumentException("Tên sheet không khớp với tháng/năm hiện tại");
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                String apartmentName = row.getCell(0).getStringCellValue();
                LocalDate consumptionDate = row.getCell(1).getLocalDateTimeCellValue().toLocalDate();
                float lastMonthWaterConsumption = (float) row.getCell(2).getNumericCellValue();
                float waterConsumption = (float) row.getCell(3).getNumericCellValue();

                Apartment apartment = apartmentRepository.findApartmentByApartmentName(apartmentName);
                List<User> users = apartment.getUsers();
                User owner = users.stream()
                        .filter(user -> "Owner".equals(user.getRole()))
                        .findFirst()
                        .orElse(null);

                Consumption consumption = new Consumption();
                consumption.setApartment(apartment);
                consumption.setConsumptionDate(consumptionDate);
                consumption.setWaterConsumption(waterConsumption);
                consumption.setLastMonthWaterConsumption(lastMonthWaterConsumption);
                consumption.setBillCreated(false);
                consumption.setUploadConsumptionUserId(createdUserId);
                consumptionRepository.save(consumption);

                ConsumptionResponseDTO responseDTO = new ConsumptionResponseDTO(
                        consumption.getConsumptionId(),
                        consumptionDate,
                        waterConsumption,
                        lastMonthWaterConsumption,
                        owner.getUserName(),
                        apartmentName
                );
                responseDTOs.add(responseDTO);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel file", e);
        }

        return responseDTOs;
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
