package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.dto.ReportDTO;

import java.util.List;

public interface ReportService {
    List<ReportDTO> getAllReports();
    ReportDTO getReportById(Long id);
    ReportDTO createReport(ReportDTO reportDTO);
    ReportDTO updateReport(Long id, ReportDTO newReportData);
    void deleteReport(Long id);
}
