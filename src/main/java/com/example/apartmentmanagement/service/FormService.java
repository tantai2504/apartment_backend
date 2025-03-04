package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.entities.Form;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FormService {
    Form uploadForm(Long userId, String formType, MultipartFile file);
    List<Form> getFormsByUser(Long userId);
    Form getFormById(Long formId);
}
