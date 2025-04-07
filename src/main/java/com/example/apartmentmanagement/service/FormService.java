package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.FormRequestDTO;
import com.example.apartmentmanagement.entities.Form;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FormService {
    Form uploadForm(Long userId, FormRequestDTO dto);
    Form editForm(Long formId, FormRequestDTO dto);

    void deleteForm(Long formId);
    List<Form> getFormsByUser(Long userId);
    Form getFormById(Long formId);
    List<Form> filterForms(String formType);
    void sendFeedback(Long formId, String feedback);
    String getFileUrl(Long formId);
}
