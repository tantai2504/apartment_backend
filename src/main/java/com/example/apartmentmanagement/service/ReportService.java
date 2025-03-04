package com.example.apartmentmanagement.service;

import com.example.apartmentmanagement.entities.Report;

import java.util.List;

public interface ReportService {
    List<Report> getAllReports();
    Report getReportById(Long id);
    Report createReport(Report report);
    Report updateReport(Long id, Report newReportData);
    void deleteReport(Long id);
}
