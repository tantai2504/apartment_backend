package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.ApartmentResponseDTO;
import com.example.apartmentmanagement.dto.UserResponseDTO;
import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.ApartmentRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.ApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApartmentServiceImpl implements ApartmentService{
    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ApartmentResponseDTO> getOwnApartments(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {

            List<Apartment> apartments = new ArrayList<>();
            List<Apartment> apartmentListFromDB = apartmentRepository.findAll();

            for (Apartment apartment : apartmentListFromDB) {
                if (user.getUserName().equals(apartment.getHouseholder())) {
                    apartments.add(apartment);
                }
            }

            return apartments.stream().map(apartment -> new ApartmentResponseDTO(
                    apartment.getApartmentId(),
                    apartment.getApartmentName(),
                    apartment.getHouseholder(),
                    apartment.getTotalNumber(),
                    apartment.getStatus(),
                    apartment.getNumberOfBedrooms(),
                    apartment.getNumberOfBathrooms(),
                    apartment.getNote(),
                    apartment.getDirection(),
                    apartment.getFloor(),
                    apartment.getArea(),
                    apartment.getUsers().stream().map(User::getUserName).toList()
            )).toList();

        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public List<ApartmentResponseDTO> showApartment() {
        return apartmentRepository.findAll().stream().map(apartment -> new ApartmentResponseDTO(
                apartment.getApartmentId(),
                apartment.getApartmentName(),
                apartment.getHouseholder(),
                apartment.getTotalNumber(),
                apartment.getStatus(),
                apartment.getNumberOfBedrooms(),
                apartment.getNumberOfBathrooms(),
                apartment.getNote(),
                apartment.getDirection(),
                apartment.getFloor(),
                apartment.getArea(),
                apartment.getUsers().stream().map(User::getUserName).toList()
        )).toList();
    }

    @Override
    public List<ApartmentResponseDTO> findApartmentsWithoutHouseholder() {
        return apartmentRepository.findAll().stream()
                .filter(apartment -> apartment.getHouseholder() == null)
                .map(apartment -> new ApartmentResponseDTO(
                        apartment.getApartmentId(),
                        apartment.getApartmentName(),
                        apartment.getHouseholder(),
                        apartment.getTotalNumber(),
                        apartment.getStatus(),
                        apartment.getNumberOfBedrooms(),
                        apartment.getNumberOfBathrooms(),
                        apartment.getNote(),
                        apartment.getDirection(),
                        apartment.getFloor(),
                        apartment.getArea(),
                        apartment.getUsers().stream().map(User::getUserName).toList()
                ))
                .toList();
    }

    @Override
    public Apartment getApartmentById(Long id) {
        Apartment apartment = apartmentRepository.findById(id).orElse(null);
        return apartment;
    }

    @Override
    public List<ApartmentResponseDTO> getApartmentByName(String name) {
        return apartmentRepository.findApartmentByApartmentNameContaining(name).stream()
                .map(apartment -> new ApartmentResponseDTO(
                        apartment.getApartmentId(),
                        apartment.getApartmentName(),
                        apartment.getHouseholder(),
                        apartment.getTotalNumber(),
                        apartment.getStatus(),
                        apartment.getNumberOfBedrooms(),
                        apartment.getNumberOfBathrooms(),
                        apartment.getNote(),
                        apartment.getDirection(),
                        apartment.getFloor(),
                        apartment.getArea(),
                        apartment.getUsers().stream().map(User::getUserName).toList()
                ))
                .toList();
    }


    @Override
    public List<ApartmentResponseDTO> totalUnrentedApartment() {
        return apartmentRepository.findAll().stream().
                map(apartment -> new ApartmentResponseDTO(
                apartment.getApartmentId(),
                apartment.getApartmentName(),
                apartment.getHouseholder(),
                apartment.getTotalNumber(),
                apartment.getStatus(),
                apartment.getNumberOfBedrooms(),
                apartment.getNumberOfBathrooms(),
                apartment.getNote(),
                apartment.getDirection(),
                apartment.getFloor(),
                apartment.getArea(),
                apartment.getUsers().stream().map(User::getUserName).toList()
        )).toList();
    }

    @Override
    public List<ApartmentResponseDTO> getOwnRentedApartment(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (!user.getRole().equals("Owner")) {
            throw new RuntimeException("Không tìm thấy owner này");
        }
        List<Apartment> apartments = user.getApartments();
        List<Apartment> returnApartments = new ArrayList<>();

        for (Apartment apartment : apartments) {
            if (apartment.getStatus().equals("rented")) {
                returnApartments.add(apartment);
            }
        }

        return returnApartments.stream().map(apartment -> new ApartmentResponseDTO(
                        apartment.getApartmentId(),
                        apartment.getApartmentName(),
                        apartment.getHouseholder(),
                        apartment.getTotalNumber(),
                        apartment.getStatus(),
                        apartment.getNumberOfBedrooms(),
                        apartment.getNumberOfBathrooms(),
                        apartment.getNote(),
                        apartment.getDirection(),
                        apartment.getFloor(),
                        apartment.getArea(),
                        apartment.getUsers().stream().map(User::getUserName).toList()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ApartmentResponseDTO> getOwnUnrentedApartment(Long userId) {

        User user = userRepository.findById(userId).orElse(null);
        if (!user.getRole().equals("Owner")) {
            throw new RuntimeException("Không tìm thấy owner này");
        }
        List<Apartment> apartments = user.getApartments();
        List<Apartment> returnApartments = new ArrayList<>();

        for (Apartment apartment : apartments) {
            if (apartment.getStatus().equals("sold")) {
                returnApartments.add(apartment);
            }
        }

        return returnApartments.stream().map(apartment -> new ApartmentResponseDTO(
                        apartment.getApartmentId(),
                        apartment.getApartmentName(),
                        apartment.getHouseholder(),
                        apartment.getTotalNumber(),
                        apartment.getStatus(),
                        apartment.getNumberOfBedrooms(),
                        apartment.getNumberOfBathrooms(),
                        apartment.getNote(),
                        apartment.getDirection(),
                        apartment.getFloor(),
                        apartment.getArea(),
                        apartment.getUsers().stream().map(User::getUserName).toList()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ApartmentResponseDTO> getOwnApartmentRented(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return apartmentRepository.findApartmentByHouseholder(user.getUserName()).stream()
                .filter(apartment -> apartment.getStatus().equalsIgnoreCase("rented"))
                .map(apartment -> new ApartmentResponseDTO(
                        apartment.getApartmentId(),
                        apartment.getApartmentName(),
                        apartment.getHouseholder(),
                        apartment.getTotalNumber(),
                        apartment.getStatus(),
                        apartment.getNumberOfBedrooms(),
                        apartment.getNumberOfBathrooms(),
                        apartment.getNote(),
                        apartment.getDirection(),
                        apartment.getFloor(),
                        apartment.getArea(),
                        apartment.getUsers().stream().map(User::getUserName).toList()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ApartmentResponseDTO updateApartment(Long apartmentId, ApartmentResponseDTO apartmentDTO) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy căn hộ"));

        if (apartmentDTO.getApartmentName() != null) {
            apartment.setApartmentName(apartmentDTO.getApartmentName());
        }
        if (apartmentDTO.getHouseholder() != null) {
            apartment.setHouseholder(apartmentDTO.getHouseholder());
        }
        if (apartmentDTO.getTotalNumber() > 0) {
            apartment.setTotalNumber(apartmentDTO.getTotalNumber());
        }
        if (apartmentDTO.getStatus() != null) {
            apartment.setStatus(apartmentDTO.getStatus());
        }
        if (apartmentDTO.getNumberOfBedrooms() > 0) {
            apartment.setNumberOfBedrooms(apartmentDTO.getNumberOfBedrooms());
        }
        if (apartmentDTO.getNumberOfBathrooms() > 0) {
            apartment.setNumberOfBathrooms(apartmentDTO.getNumberOfBathrooms());
        }
        if (apartmentDTO.getNote() != null) {
            apartment.setNote(apartmentDTO.getNote());
        }
        if (apartmentDTO.getDirection() != null) {
            apartment.setDirection(apartmentDTO.getDirection());
        }
        if (apartmentDTO.getFloor() != null) {
            apartment.setFloor(apartmentDTO.getFloor());
        }
        if (apartmentDTO.getArea() != null) {
            apartment.setArea(apartmentDTO.getArea());
        }

        apartment = apartmentRepository.save(apartment);

        return new ApartmentResponseDTO(
                apartment.getApartmentId(),
                apartment.getApartmentName(),
                apartment.getHouseholder(),
                apartment.getTotalNumber(),
                apartment.getStatus(),
                apartment.getNumberOfBedrooms(),
                apartment.getNumberOfBathrooms(),
                apartment.getNote(),
                apartment.getDirection(),
                apartment.getFloor(),
                apartment.getArea(),
                apartment.getUsers().stream().map(User::getUserName).toList()
        );
    }

    @Override
    public ApartmentResponseDTO createApartment(ApartmentResponseDTO apartmentDTO) {
        if (apartmentDTO.getApartmentName() == null || apartmentDTO.getApartmentName().trim().isEmpty()) {
            throw new RuntimeException("Tên căn hộ không được để trống");
        }

        Apartment existingApartment = apartmentRepository.findApartmentByApartmentName(apartmentDTO.getApartmentName());
        if (existingApartment != null) {
            throw new RuntimeException("Căn hộ với tên này đã tồn tại");
        }

        Apartment apartment = new Apartment();
        apartment.setApartmentName(apartmentDTO.getApartmentName());
        apartment.setHouseholder(apartmentDTO.getHouseholder());
        apartment.setTotalNumber(apartmentDTO.getTotalNumber());
        apartment.setStatus(apartmentDTO.getStatus() != null ? apartmentDTO.getStatus() : "unrented");
        apartment.setNumberOfBedrooms(apartmentDTO.getNumberOfBedrooms());
        apartment.setNumberOfBathrooms(apartmentDTO.getNumberOfBathrooms());
        apartment.setNote(apartmentDTO.getNote());
        apartment.setDirection(apartmentDTO.getDirection());
        apartment.setFloor(apartmentDTO.getFloor());
        apartment.setArea(apartmentDTO.getArea());

        apartment = apartmentRepository.save(apartment);

        return new ApartmentResponseDTO(
                apartment.getApartmentId(),
                apartment.getApartmentName(),
                apartment.getHouseholder(),
                apartment.getTotalNumber(),
                apartment.getStatus(),
                apartment.getNumberOfBedrooms(),
                apartment.getNumberOfBathrooms(),
                apartment.getNote(),
                apartment.getDirection(),
                apartment.getFloor(),
                apartment.getArea(),
                List.of()
        );
    }

    @Override
    public void deleteApartment(Long apartmentId) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy căn hộ"));
        if ("rented".equals(apartment.getStatus())) {
            throw new RuntimeException("Không thể xóa căn hộ đang được thuê");
        }
        if (apartment.getUsers() != null && !apartment.getUsers().isEmpty()) {
            throw new RuntimeException("Không thể xóa căn hộ đang có người ở");
        }
        apartmentRepository.delete(apartment);
    }

    @Override
    public List<UserResponseDTO> getRentorByApartment(String apartmentName) {
        Apartment apartment = apartmentRepository.findApartmentByApartmentName(apartmentName);

        if (apartment == null) {
            throw new RuntimeException("Không tìm thấy căn hộ này");
        }

        List<User> users = apartment.getUsers();

        return users.stream()
                .filter(user -> "Rentor".equalsIgnoreCase(user.getRole()))
                .map(user -> new UserResponseDTO(
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
                        user.getRole()
                ))
                .toList();
    }

    @Override
    public List<ApartmentResponseDTO> getApartmentByRenter(Long userId) {
        List<Apartment> apartments = apartmentRepository.findApartmentsByRenterId(userId);
        return apartments.stream().map(apartment -> new ApartmentResponseDTO(
                        apartment.getApartmentId(),
                        apartment.getApartmentName(),
                        apartment.getHouseholder(),
                        apartment.getTotalNumber(),
                        apartment.getStatus(),
                        apartment.getNumberOfBedrooms(),
                        apartment.getNumberOfBathrooms(),
                        apartment.getNote(),
                        apartment.getDirection(),
                        apartment.getFloor(),
                        apartment.getArea(),
                        apartment.getUsers().stream().map(User::getUserName).toList()
                ))
                .collect(Collectors.toList());
    }

}
