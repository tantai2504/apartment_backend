package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.ReportDTO;
import com.example.apartmentmanagement.entities.Report;

import java.util.List;

public interface ReportService {
    List<Report> getAllReports();
    Report getReportById(Long id);
    ReportDTO createReport(ReportDTO reportDTO);
    Report updateReport(Long id, Report newReportData);
    void deleteReport(Long id);
}
