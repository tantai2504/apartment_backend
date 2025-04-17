package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.FacilityRequestDTO;
import com.example.apartmentmanagement.dto.FacilityResponseDTO;
import com.example.apartmentmanagement.entities.*;
import com.example.apartmentmanagement.repository.FacilityImagesRepository;
import com.example.apartmentmanagement.repository.FacilityRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.FacilityService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FacilityServiceImpl implements FacilityService {

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private FacilityImagesRepository facilityImagesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Override
    public List<FacilityResponseDTO> getFacilities() {
        return facilityRepository.findAll().stream().map(facility -> new FacilityResponseDTO(
                facility.getUser().getUserName(),
                facility.getUser().getPhone(),
                facility.getUser().getEmail(),
                facility.getFacilityId(),
                facility.getFacilityContent(),
                facility.getFacilityHeader(),
                facility.getVerifiedCheck(),
                facility.getFacilityPostingDate(),
                facility.getFacilityImages().stream().map(FacilityImages::getImageUrl).toList()
        )).toList();
    }

    @Override
    public List<FacilityResponseDTO> getRejectedFacilities() {
        return facilityRepository.findAll().stream()
                .filter(facility -> "rejected".equals(facility.getVerifiedCheck()))
                .map(facility -> new FacilityResponseDTO(
                        facility.getUser().getUserName(),
                        facility.getUser().getPhone(),
                        facility.getUser().getEmail(),
                        facility.getFacilityId(),
                        facility.getFacilityContent(),
                        facility.getFacilityHeader(),
                        facility.getVerifiedCheck(),
                        facility.getFacilityPostingDate(),
                        facility.getFacilityImages().stream()
                                .map(FacilityImages::getImageUrl)
                                .toList()
                ))
                .toList();
    }


    @Override
    public List<FacilityResponseDTO> getVerifiedFacilities() {
        return facilityRepository.findAll().stream()
                .filter(facility -> "verified".equals(facility.getVerifiedCheck()))
                .map(facility -> new FacilityResponseDTO(
                        facility.getUser().getUserName(),
                        facility.getUser().getPhone(),
                        facility.getUser().getEmail(),
                        facility.getFacilityId(),
                        facility.getFacilityContent(),
                        facility.getFacilityHeader(),
                        facility.getVerifiedCheck(),
                        facility.getFacilityPostingDate(),
                        facility.getFacilityImages().stream()
                                .map(FacilityImages::getImageUrl)
                                .toList()
                ))
                .toList();
    }

    @Override
    public List<FacilityResponseDTO> getUnverifiedFacilities() {
        System.out.println("hello");
        return facilityRepository.findAll().stream()
                .filter(facility -> "unverified".equals(facility.getVerifiedCheck()))
                .map(facility -> new FacilityResponseDTO(
                        facility.getUser().getUserName(),
                        facility.getUser().getPhone(),
                        facility.getUser().getEmail(),
                        facility.getFacilityId(),
                        facility.getFacilityContent(),
                        facility.getFacilityHeader(),
                        facility.getVerifiedCheck(),
                        facility.getFacilityPostingDate(),
                        facility.getFacilityImages().stream()
                                .map(FacilityImages::getImageUrl)
                                .toList()
                ))
                .toList();
    }

    @Override
    public FacilityResponseDTO createFacility(FacilityRequestDTO facilityRequestDTO, List<MultipartFile> imageFiles) {

        User user = userRepository.findById(facilityRequestDTO.getUserId()).get();

        Facility facility = new Facility();
        facility.setUser(user);

        if (facilityRequestDTO.getFacilityPostContent().isEmpty() || facilityRequestDTO.getFacilityPostContent() == null) {
            throw new RuntimeException("Không thể bỏ trống nội dung");
        }

        facility.setFacilityContent(facilityRequestDTO.getFacilityPostContent());
        facility.setVerifiedCheck("unverified");
        facility.setFacilityPostingDate(LocalDateTime.now());

        List<String> facilityImageUrl = imageUploadService.uploadMultipleImages(imageFiles);
        List<FacilityImages> facilityImages = new ArrayList<>();

        for (String imageUrl : facilityImageUrl) {
            FacilityImages facilityImage = new FacilityImages();
            facilityImage.setImageUrl(imageUrl);
            facilityImage.setFacility(facility);
            facilityImages.add(facilityImage);
        }

        facility.setFacilityImages(facilityImages);

        facilityRepository.save(facility);
        facilityImagesRepository.saveAll(facilityImages);

        return new FacilityResponseDTO(
                facility.getUser().getUserName(),
                facility.getUser().getPhone(),
                facility.getUser().getEmail(),
                facility.getFacilityId(),
                facility.getFacilityContent(),
                facility.getFacilityHeader(),
                facility.getVerifiedCheck(),
                facility.getFacilityPostingDate(),
                facility.getFacilityImages().stream().map(FacilityImages::getImageUrl).toList()
        );
    }

    @Override
    @Transactional
    public void deleteFacility(Long id) {
        Optional<Facility> facility = facilityRepository.findById(id);
        if (facility.isPresent()) {
            Facility facility1 = facility.get();

            if (facility1.getFacilityImages() != null) {
                for (FacilityImages image : facility1.getFacilityImages()) {
                    image.setFacility(null);
                }
                facility1.getFacilityImages().clear();
            }
            facilityRepository.delete(facility1);
        }
    }

    @Override
    public FacilityResponseDTO getFacility(Long id) {

        Facility facility = facilityRepository.findById(id).orElse(null);

        if (facility == null) {
            throw new RuntimeException("Không tìm thấy bài post dịch vụ này");
        }

        return new FacilityResponseDTO(
                facility.getUser().getUserName(),
                facility.getUser().getPhone(),
                facility.getUser().getEmail(),
                facility.getFacilityId(),
                facility.getFacilityContent(),
                facility.getFacilityHeader(),
                facility.getVerifiedCheck(),
                facility.getFacilityPostingDate(),
                facility.getFacilityImages().stream().map(FacilityImages::getImageUrl).toList()
        );
    }

    @Override
    public FacilityResponseDTO updateFacility(Long id, FacilityRequestDTO facilityRequestDTO, List<MultipartFile> imageFiles) {
        Facility facility = facilityRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng này"));
        User user = userRepository.findById(facilityRequestDTO.getUserId()).orElseThrow();

        facility.setUser(user);

        if (facilityRequestDTO.getFacilityPostContent() == null || facilityRequestDTO.getFacilityPostContent().isEmpty()) {
            throw new RuntimeException("Không thể bỏ trống nội dung");
        }

        facility.setVerifiedUserId(null);
        facility.setFacilityContent(facilityRequestDTO.getFacilityPostContent());
        facility.setVerifiedCheckDate(null);
        facility.setVerifiedCheck("unverified");
        facility.setFacilityPostingDate(LocalDateTime.now());

        if (imageFiles != null && !imageFiles.isEmpty()) {
            facility.getFacilityImages().clear();

            List<String> facilityImagesUrl = imageUploadService.uploadMultipleImages(imageFiles);
            List<FacilityImages> facilityImagesList = facilityImagesUrl.stream().map(url -> {
                FacilityImages facilityImages = new FacilityImages();
                facilityImages.setImageUrl(url);
                facilityImages.setFacility(facility);
                return facilityImages;
            }).toList();

            facility.getFacilityImages().addAll(facilityImagesList);
        } else {
            facility.getFacilityImages().clear();
        }

        facilityRepository.save(facility);

        return new FacilityResponseDTO(
                facility.getUser().getUserName(),
                facility.getUser().getPhone(),
                facility.getUser().getEmail(),
                facility.getFacilityId(),
                facility.getFacilityContent(),
                facility.getFacilityHeader(),
                facility.getVerifiedCheck(),
                facility.getFacilityPostingDate(),
                facility.getFacilityImages().stream().map(FacilityImages::getImageUrl).toList()
        );
    }

    @Override
    public FacilityResponseDTO rejectFacility(Long id, String reason, Long verifiedUserId) {
        Facility facility = facilityRepository.findById(id).orElse(null);

        if (facility == null) {
            throw new RuntimeException("Facility not found");
        }

        facility.setVerifiedCheck("rejected");
        facility.setReason(reason);
        facility.setVerifiedCheckDate(LocalDateTime.now());
        facility.setVerifiedUserId(verifiedUserId);
        facilityRepository.save(facility);

        User verifiedUser = userRepository.findById(verifiedUserId).orElse(null);

        if (verifiedUser == null) {
            throw new RuntimeException("User not found");
        }

        return new FacilityResponseDTO(
                facility.getUser().getUserName(),
                facility.getUser().getPhone(),
                facility.getUser().getEmail(),
                facility.getFacilityId(),
                facility.getFacilityContent(),
                facility.getFacilityHeader(),
                facility.getVerifiedCheck(),
                facility.getFacilityPostingDate(),
                facility.getVerifiedCheckDate(),
                facility.getReason(),
                verifiedUser.getUserName(),
                facility.getFacilityImages().stream().map(FacilityImages::getImageUrl).toList()
        );
    }

    @Override
    public FacilityResponseDTO verifyFacility(Long facilityId, Long verifiedUserId) {
        Facility facility = facilityRepository.findById(facilityId).orElse(null);

        if (facility == null) {
            throw new RuntimeException("Facility not found");
        }

        facility.setVerifiedCheck("verified");
        facility.setVerifiedCheckDate(LocalDateTime.now());
        facility.setVerifiedUserId(verifiedUserId);
        facility.setReason(null);
        facilityRepository.save(facility);

        User verifiedUser = userRepository.findById(verifiedUserId).orElse(null);

        if (verifiedUser == null) {
            throw new RuntimeException("User not found");
        }

        return new FacilityResponseDTO(
                facility.getUser().getUserName(),
                facility.getUser().getPhone(),
                facility.getUser().getEmail(),
                facility.getFacilityId(),
                facility.getFacilityContent(),
                facility.getFacilityHeader(),
                facility.getVerifiedCheck(),
                facility.getFacilityPostingDate(),
                facility.getVerifiedCheckDate(),
                facility.getFacilityImages().stream().map(FacilityImages::getImageUrl).toList(),
                verifiedUser.getUserName()
        );
    }
}
