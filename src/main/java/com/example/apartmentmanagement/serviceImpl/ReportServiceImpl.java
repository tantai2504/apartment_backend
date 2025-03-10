package com.example.apartmentmanagement.serviceImpl;

import com.example.apartmentmanagement.dto.ReportDTO;
import com.example.apartmentmanagement.entities.Report;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.repository.ReportRepository;
import com.example.apartmentmanagement.repository.UserRepository;
import com.example.apartmentmanagement.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    // 🔹 Chuyển đổi từ `Report` sang `ReportDTO`
    private ReportDTO convertToDto(Report report) {
        return new ReportDTO(
                report.getReportId(),
                report.getReportContent(),
                report.getReportDate(),
                report.isReportCheck(),
                report.getUser().getUserId()
        );
    }

    // 🔹 Chuyển đổi từ `ReportDTO` sang `Report`
    private Report convertToEntity(ReportDTO reportDTO, User user) {
        Report report = new Report();
        report.setReportId(reportDTO.getReportId());
        report.setReportContent(reportDTO.getReportContent());
        report.setReportDate(reportDTO.getReportDate());
        report.setReportCheck(reportDTO.isReportCheck());
        report.setUser(user);
        return report;
    }

    @Override
    public List<ReportDTO> getAllReports() {
        return reportRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReportDTO getReportById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
        return convertToDto(report);
    }

    @Override
    public ReportDTO createReport(ReportDTO reportDTO) {
        User user = userRepository.findById(reportDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + reportDTO.getUserId()));

        Report report = convertToEntity(reportDTO, user);
        reportRepository.save(report);
        return convertToDto(report);
    }

    @Override
    public ReportDTO updateReport(Long id, ReportDTO newReportData) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));

        // Cập nhật nội dung nếu có
        if (newReportData.getReportContent() != null) {
            report.setReportContent(newReportData.getReportContent());
        }
        if (newReportData.getReportDate() != null) {
            report.setReportDate(newReportData.getReportDate());
        }
        report.setReportCheck(newReportData.isReportCheck());

        // Cập nhật User nếu có
        if (newReportData.getUserId() != null) {
            User user = userRepository.findById(newReportData.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + newReportData.getUserId()));
            report.setUser(user);
        }

        reportRepository.save(report);
        return convertToDto(report);
    }

    @Override
    public void deleteReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
        reportRepository.delete(report);
    }
}
