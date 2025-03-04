package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.entities.Form;
import com.example.apartmentmanagement.service.FormService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
public class FormController {

    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }

    @PostMapping(value = "/upload/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Form> uploadForm(
            @PathVariable Long userId,
            @RequestParam("formType") String formType,
            @RequestParam("file") MultipartFile file
    ) {
        System.out.println("UserID: " + userId);
        System.out.println("FormType: " + formType);
        System.out.println("File: " + file.getOriginalFilename());
        return ResponseEntity.ok(formService.uploadForm(userId, formType, file));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Form>> getFormsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(formService.getFormsByUser(userId));
    }

    @GetMapping("/{formId}")
    public ResponseEntity<Form> getFormById(@PathVariable Long formId) {
        return ResponseEntity.ok(formService.getFormById(formId));
    }
}
