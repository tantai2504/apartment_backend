package com.example.apartmentmanagement.serviceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.apartmentmanagement.dto.FormRequestDTO;
import com.example.apartmentmanagement.entities.Apartment;
import com.example.apartmentmanagement.entities.Form;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.ApartmentRepository;
import com.example.apartmentmanagement.repository.FormRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.FormService;
import com.example.apartmentmanagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class FormServiceImpl implements FormService {

    private final FormRepository formRepository;
    private final UserRepository userRepository;
    private final ApartmentRepository apartmentRepository;
    private final Cloudinary cloudinary;
    @Autowired
    private NotificationService notificationService;

    public FormServiceImpl(FormRepository formRepository, UserRepository userRepository,ApartmentRepository apartmentRepository, Cloudinary cloudinary) {
        this.formRepository = formRepository;
        this.userRepository = userRepository;
        this.apartmentRepository = apartmentRepository;
        this.cloudinary = cloudinary;
    }

    @Override
    public List<Form> getAllForms() {
        return formRepository.findAll();
    }

    @Override
    public Form uploadForm(Long userId, FormRequestDTO dto) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Apartment apartment = apartmentRepository.findById(dto.getApartmentId())
                    .orElseThrow(() -> new RuntimeException("Apartment not found"));
            MultipartFile file = dto.getFile();
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("File must not be empty");
            }
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto")
            );

            Form form = new Form();
            form.setFormType(dto.getFormType());
            form.setStatus("pending");
            form.setFileUrl(uploadResult.get("secure_url").toString());
            form.setFileName(dto.getFile().getOriginalFilename());
            form.setCreatedAt(new Date());
            form.setUser(user);
            form.setApartment(apartment);
            form.setReason(dto.getReason());

            if ("approved".equalsIgnoreCase(dto.getStatus())) {
                form.setExecutedAt(new Date());
            }

            return formRepository.save(form);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    @Override
    public Form editForm(Long formId, FormRequestDTO dto) {
        try {
            Form form = formRepository.findById(formId)
                    .orElseThrow(() -> new RuntimeException("Form not found"));
            Apartment apartment = apartmentRepository.findById(dto.getApartmentId())
                    .orElseThrow(() -> new RuntimeException("Apartment not found"));
            MultipartFile file = dto.getFile();
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("File must not be empty");
            }
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto")
            );

            form.setFormType(dto.getFormType());
            form.setStatus(dto.getStatus());
            form.setFileUrl(uploadResult.get("secure_url").toString());
            form.setFileName(dto.getFile().getOriginalFilename());
            form.setCreatedAt(new Date());
            form.setApartment(apartment);
            form.setReason(dto.getReason());

            if ("approved".equalsIgnoreCase(dto.getStatus())) {
                form.setExecutedAt(new Date());
            }

            return formRepository.save(form);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }


    @Override
    public void deleteForm(Long formId) {
        if (!formRepository.existsById(formId)) {
            throw new RuntimeException("Form not found");
        }
        formRepository.deleteById(formId);
    }

    @Override
    public List<Form> getFormsByUser(Long userId) {
        return formRepository.findByUserUserId(userId);
    }

    @Override
    public Form getFormById(Long formId) {
        return formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found"));
    }

    @Override
    public List<Form> filterForms(String formType) {
        return formRepository.findByFormType(formType);
    }

    @Override
    public void sendFeedback(Long formId, String feedback) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found"));
        form.setFormType(form.getFormType() + " | Feedback: " + feedback);
        formRepository.save(form);
    }

    @Override
    public String getFileUrl(Long formId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found"));
        return form.getFileUrl();
    }
    @Override
    public Form approveForm(Long formId, String status) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found"));

        if (!"pending".equalsIgnoreCase(form.getStatus())) {
            throw new RuntimeException("Only pending forms can be approved/rejected");
        }

        if (!"approved".equalsIgnoreCase(status) && !"rejected".equalsIgnoreCase(status)) {
            throw new RuntimeException("Invalid status. Must be 'approved' or 'rejected'");
        }

        form.setStatus(status.toLowerCase());

        if ("approved".equalsIgnoreCase(status)) {
            form.setExecutedAt(new Date());
            // Tạo thông báo cho admin
            notificationService.createAndBroadcastNotification(
                    "Đơn từ " + form.getFileName() + " đã được duyệt",
                    "form",
                    form.getUser().getUserId()
            );
        }

        if ("rejected".equalsIgnoreCase(status)) {
            // Tạo thông báo cho admin
            notificationService.createAndBroadcastNotification(
                    "Đơn từ " + form.getFileName() + " đã bị từ chối",
                    "form",
                    form.getUser().getUserId()
            );
        }


        return formRepository.save(form);
    }
}
