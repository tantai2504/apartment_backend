package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.entities.Form;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FormService {
    Form uploadForm(Long userId, String formType, MultipartFile file);
    Form editForm(Long formId, String formType, MultipartFile file);
    void deleteForm(Long formId);
    List<Form> getFormsByUser(Long userId);
    Form getFormById(Long formId);
    List<Form> filterForms(String formType);
    void sendFeedback(Long formId, String feedback);
    String getFileUrl(Long formId);
}
