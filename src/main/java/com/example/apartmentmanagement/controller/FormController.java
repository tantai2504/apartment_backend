package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.FormRequestDTO;
import com.example.apartmentmanagement.dto.FormResponseDTO;
import com.example.apartmentmanagement.entities.Form;
import com.example.apartmentmanagement.service.FormService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/forms")
public class FormController {

    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }

    private FormResponseDTO toDTO(Form form) {
        FormResponseDTO dto = new FormResponseDTO();
        dto.setFormId(form.getFormId());
        dto.setFormType(form.getFormType());
        dto.setFileUrl(form.getFileUrl());
        dto.setFileName(form.getFileName());
        dto.setCreatedAt(form.getCreatedAt());
        dto.setExecutedAt(form.getExecutedAt());
        dto.setStatus(form.getStatus());
        dto.setReason(form.getReason());
        dto.setUserId(form.getUser().getUserId());
        dto.setApartmentId(form.getApartment().getApartmentId());
        return dto;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadForm(
            @RequestParam("userId") Long userId,
            @RequestParam("formType") String formType,
            @RequestParam("apartmentId") String apartmentName,
            @RequestParam("reason") String reason,
            @RequestParam("file") MultipartFile file
    ) {
        FormRequestDTO dto = new FormRequestDTO(formType, reason, apartmentName, file);
        Map<String, Object> response = new HashMap<>();
        try {
            Form form = formService.uploadForm(userId, dto);
            FormResponseDTO formResponse = toDTO(form);
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", formResponse);
            response.put("message", "Form uploaded successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping(value = "/edit/{formId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> editForm(
            @PathVariable Long formId,
            @RequestParam("formType") String formType,
            @RequestParam("apartmentId") String apartmentName,
            @RequestParam("reason") String reason,
            @RequestParam("file") MultipartFile file
    ) {
        FormRequestDTO dto = new FormRequestDTO(formType, reason, apartmentName, file);
        Map<String, Object> response = new HashMap<>();
        try {
            Form form = formService.editForm(formId, dto);
            FormResponseDTO formResponse = toDTO(form);
            response.put("status", HttpStatus.OK.value());
            response.put("data", formResponse);
            response.put("message", "Form edited successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getFormsByUser(@PathVariable Long userId) {
        List<Form> forms = formService.getFormsByUser(userId);
        Map<String, Object> response = new HashMap<>();
        if (forms.isEmpty()) {
            response.put("message", "No forms found for this user");
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            List<FormResponseDTO> dtos = forms.stream().map(this::toDTO).collect(Collectors.toList());
            response.put("status", HttpStatus.OK.value());
            response.put("forms", dtos);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @GetMapping("/{formId}")
    public ResponseEntity<Object> getFormById(@PathVariable Long formId) {
        Form form = formService.getFormById(formId);
        Map<String, Object> response = new HashMap<>();
        if (form == null) {
            response.put("message", "Form not found");
            response.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            response.put("status", HttpStatus.OK.value());
            response.put("form", toDTO(form));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @DeleteMapping("/delete/{formId}")
    public ResponseEntity<Object> deleteForm(@PathVariable Long formId) {
        formService.deleteForm(formId);
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Form deleted successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<Object> filterForms(@RequestParam String formType) {
        List<Form> forms = formService.filterForms(formType);
        List<FormResponseDTO> dtos = forms.stream().map(this::toDTO).collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("forms", dtos);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllForms() {
        List<Form> forms = formService.getAllForms();
        List<FormResponseDTO> dtos = forms.stream().map(this::toDTO).collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("forms", dtos);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/approve/{formId}")
    public ResponseEntity<Object> approveForm(
            @PathVariable Long formId,
            @RequestParam String status
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Form form = formService.approveForm(formId, status);
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Form status updated successfully");
            response.put("form", toDTO(form));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
