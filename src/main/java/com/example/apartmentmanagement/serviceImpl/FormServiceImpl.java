package com.example.apartmentmanagement.serviceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.apartmentmanagement.entities.Form;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.FormRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.FormService;
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
    private final Cloudinary cloudinary;

    public FormServiceImpl(FormRepository formRepository, UserRepository userRepository, Cloudinary cloudinary) {
        this.formRepository = formRepository;
        this.userRepository = userRepository;
        this.cloudinary = cloudinary;
    }

    @Override
    public Form uploadForm(Long userId, String formType, MultipartFile file) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            Form form = new Form();
            form.setFormType(formType);
            form.setFileUrl(uploadResult.get("secure_url").toString());
            form.setFileName(file.getOriginalFilename());
            form.setCreatedAt(new Date());
            form.setUser(user);

            return formRepository.save(form);

        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
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
}
