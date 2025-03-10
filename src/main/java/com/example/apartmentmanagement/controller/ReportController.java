package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.ReportDTO;
import com.example.apartmentmanagement.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // Lấy danh sách tất cả báo cáo
    @GetMapping
    public ResponseEntity<List<ReportDTO>> getAllReports() {
        List<ReportDTO> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    //  Lấy báo cáo theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ReportDTO> getReportById(@PathVariable Long id) {
        ReportDTO report = reportService.getReportById(id);
        return ResponseEntity.ok(report);
    }

    //  Tạo mới một báo cáo
    @PostMapping
    public ResponseEntity<ReportDTO> createReport(@RequestBody ReportDTO reportDTO) {
        ReportDTO savedReport = reportService.createReport(reportDTO);
        return ResponseEntity.status(201).body(savedReport); // 201 Created
    }

    //  Cập nhật báo cáo
    @PutMapping("/{id}")
    public ResponseEntity<ReportDTO> updateReport(@PathVariable Long id, @RequestBody ReportDTO newReportData) {
        ReportDTO updatedReport = reportService.updateReport(id, newReportData);
        return ResponseEntity.ok(updatedReport);
    }

    //  Xóa báo cáo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
