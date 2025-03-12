package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.ReportDTO;
import com.example.apartmentmanagement.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // Lấy danh sách tất cả báo cáo
    @GetMapping
    public ResponseEntity<Object> getAllReports() {
        List<ReportDTO> reports = reportService.getAllReports();
        Map<String, Object> response = new HashMap<>();
        if (reports.isEmpty()) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Chưa có report nào");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("status", HttpStatus.OK.value());
        response.put("data", reports);
        return ResponseEntity.ok(response);
    }

    //  Lấy báo cáo theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getReportById(@PathVariable Long id) {
        ReportDTO report = reportService.getReportById(id);
        Map<String, Object> response = new HashMap<>();
        if (report == null) {
            response.put("status", HttpStatus.NOT_FOUND.value());
            response.put("message", "Không tìm thấy report này");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("status", HttpStatus.OK.value());
        response.put("data", report);
        return ResponseEntity.ok(response);
    }

    //  Tạo mới một báo cáo
    @PostMapping
    public ResponseEntity<Object> createReport(@RequestBody ReportDTO reportDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            ReportDTO savedReport = reportService.createReport(reportDTO);
            response.put("data", savedReport);
            response.put("status", HttpStatus.CREATED.value());
            response.put("message", "Khởi tạo thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    //  Cập nhật báo cáo
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateReport(@PathVariable Long id, @RequestBody ReportDTO newReportData) {
        Map<String, Object> response = new HashMap<>();
        try {
            ReportDTO updatedReport = reportService.updateReport(id, newReportData);
            response.put("status", HttpStatus.CREATED.value());
            response.put("message", "Cập nhật thành công");
            response.put("data", updatedReport);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    //  Xóa báo cáo
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
