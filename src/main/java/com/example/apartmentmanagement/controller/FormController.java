package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.FormRequestDTO;
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

@RestController
@RequestMapping("/api/forms")
public class FormController {

    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }

    @PostMapping(value = "/upload/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> uploadForm(
            @PathVariable Long userId,
            @RequestPart("dto") FormRequestDTO dto,
            @RequestPart("file") MultipartFile file
    ) {
        dto.setFile(file);
        Map<String, Object> response = new HashMap<>();
        try {
            Form form = formService.uploadForm(userId, dto);
            response.put("status", HttpStatus.CREATED.value());
            response.put("data", form);
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
            @RequestPart("dto") FormRequestDTO dto,
            @RequestPart("file") MultipartFile file
    ) {
        dto.setFile(file);
        Map<String, Object> response = new HashMap<>();
        try {
            Form form = formService.editForm(formId, dto);
            response.put("status", HttpStatus.OK.value());
            response.put("data", form);
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
            response.put("status", HttpStatus.OK.value());
            response.put("forms", forms);
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
            response.put("form", form);
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
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("forms", forms);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/feedback/{formId}")
    public ResponseEntity<Object> sendFeedback(@PathVariable Long formId, @RequestParam("feedback") String feedback) {
        formService.sendFeedback(formId, feedback);
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Feedback sent successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/download/{formId}")
    public ResponseEntity<Object> downloadForm(@PathVariable Long formId) {
        String fileUrl = formService.getFileUrl(formId);
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("fileUrl", fileUrl);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
