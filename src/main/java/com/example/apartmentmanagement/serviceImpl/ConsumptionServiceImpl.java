package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.ApartmentResponseInUserDTO;
import com.example.apartmentmanagement.dto.ConsumptionRequestDTO;
import com.example.apartmentmanagement.dto.ConsumptionResponseDTO;
import com.example.apartmentmanagement.dto.UserResponseDTO;
import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.Consumption;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.exception.ConsumptionValidationException;
import com.example.apartmentmanagement.repository.ApartmentRepository;
import com.example.apartmentmanagement.repository.ConsumptionRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.ConsumptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
        List<ConsumptionResponseDTO> responseDTOs = new ArrayList<>();
        List<Consumption> validConsumptions = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            String firstSheetName = workbook.getSheetName(0);
            String expectedSheetName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            if (!firstSheetName.equals(expectedSheetName)) {
                throw new IllegalArgumentException("Tên sheet không khớp với tháng/năm hiện tại");
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0 || row == null) continue;

                Cell apartmentCell = row.getCell(0);
                Cell dateCell = row.getCell(1);
                Cell currentMonthCell = row.getCell(2);

                try {
                    String apartmentName = apartmentCell.getStringCellValue();
                    LocalDate consumptionDate = dateCell.getLocalDateTimeCellValue().toLocalDate();
                    float waterConsumption = (float) currentMonthCell.getNumericCellValue();

                    Apartment apartment = apartmentRepository.findApartmentByApartmentName(apartmentName);
                    if (apartment == null) {
                        errorMessages.add("Dòng " + (row.getRowNum() + 1) + ": Không tìm thấy căn hộ " + apartmentName);
                        continue;
                    }

                    // Lấy consumption tháng trước
                    LocalDate lastMonth = consumptionDate.minusMonths(1);
                    Consumption lastMonthConsumption = consumptionRepository
                            .findTopByApartmentAndConsumptionDateBetweenOrderByConsumptionDateDesc(
                                    apartment,
                                    lastMonth.withDayOfMonth(1),
                                    lastMonth.withDayOfMonth(lastMonth.lengthOfMonth())
                            );
                    LocalDate currentMonth = LocalDate.now().withDayOfMonth(1); // Đầu tháng hiện tại

                    if (!consumptionDate.isAfter(currentMonth.minusDays(1)) || !consumptionDate.isBefore(currentMonth.plusMonths(1).minusDays(1))) {
                        errorMessages.add("Dòng " + (row.getRowNum() + 1) + ": Ngày không phải của tháng hiện tại");
                        continue;
                    }

                    Consumption existingConsumption = consumptionRepository
                            .findTopByApartmentAndConsumptionDateBetweenOrderByConsumptionDateDesc(
                                    apartment,
                                    currentMonth,
                                    currentMonth.withDayOfMonth(currentMonth.lengthOfMonth())
                            );
                    if (existingConsumption != null) {
                        errorMessages.add("Dòng " + (row.getRowNum() + 1) + ": Căn hộ " + apartmentName + " đã có dữ liệu tiêu thụ nước cho tháng này");
                        continue;
                    }

                    float lastMonthWaterConsumption = lastMonthConsumption != null
                            ? lastMonthConsumption.getWaterConsumption()
                            : 0f;

                    if (waterConsumption < lastMonthWaterConsumption) {
                        errorMessages.add("Dòng " + (row.getRowNum() + 1) + ": Trị số tiêu thụ nước tháng trước không được bé hơn tháng" +
                                " này: " + apartmentName + " - Tháng trước: " + lastMonthWaterConsumption + " >< Tháng này: " + waterConsumption);
                        continue;
                    }

                    Consumption consumption = new Consumption();
                    consumption.setApartment(apartment);
                    consumption.setConsumptionDate(consumptionDate);
                    consumption.setWaterConsumption(waterConsumption);
                    consumption.setLastMonthWaterConsumption(lastMonthWaterConsumption);
                    consumption.setBillCreated(false);
                    consumption.setUploadConsumptionUserId(createdUserId);

                    validConsumptions.add(consumption);
                } catch (Exception e) {
                    errorMessages.add("Dòng " + (row.getRowNum() + 1) + ": Dữ liệu không hợp lệ");
                }
            }
        }

        if (!errorMessages.isEmpty()) {
            throw new ConsumptionValidationException(errorMessages);
        }

        List<Consumption> saved = consumptionRepository.saveAll(validConsumptions);

        for (Consumption consumption : saved) {
            Apartment apartment = consumption.getApartment();
            List<User> users = apartment.getUsers();
            User owner = users.stream()
                    .filter(user -> "Owner".equals(user.getRole()))
                    .findFirst()
                    .orElse(null);

            ConsumptionResponseDTO dto = new ConsumptionResponseDTO(
                    consumption.getConsumptionId(),
                    consumption.getConsumptionDate(),
                    consumption.getLastMonthWaterConsumption(),
                    consumption.getWaterConsumption(),
                    owner != null ? owner.getUserName() : "Unknown",
                    apartment.getApartmentName()
            );
            responseDTOs.add(dto);
        }

        return responseDTOs;
    }

    @Override
    public ConsumptionResponseDTO getConsumptionById(Long consumptionId) {
        Consumption consumption = consumptionRepository.findById(consumptionId).orElse(null);

        User user = userRepository.findByUserName(consumption.getApartment().getHouseholder());

        return new ConsumptionResponseDTO(
                consumption.getConsumptionId(),
                consumption.getConsumptionDate(),
                consumption.getLastMonthWaterConsumption(),
                consumption.getWaterConsumption(),
                user.getFullName(),
                consumption.getApartment().getApartmentName()
        );
    }

    @Override
    public ConsumptionResponseDTO updateConsumption(Long consumptionId, float waterConsumption) {
        Consumption consumption = consumptionRepository.findById(consumptionId).orElse(null);

        if (consumption == null) {
            throw new RuntimeException("consumption not found");
        }

        User user = userRepository.findByUserName(consumption.getApartment().getHouseholder());

        consumption.setWaterConsumption(waterConsumption);
        consumption.setConsumptionDate(LocalDate.now());
        consumptionRepository.save(consumption);
        return new ConsumptionResponseDTO(
                consumption.getConsumptionId(),
                consumption.getConsumptionDate(),
                consumption.getLastMonthWaterConsumption(),
                consumption.getWaterConsumption(),
                user.getFullName(),
                consumption.getApartment().getApartmentName()
        );
    }

    @Override
    public void deleteConsumption(Long consumptionId) {

    }
}
