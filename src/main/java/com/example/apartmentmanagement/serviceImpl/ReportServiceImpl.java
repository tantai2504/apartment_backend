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

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userInfoRepository; // Giả sử bạn quản lý User ở đây

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
    }

    @Override
    public ReportDTO createReport(ReportDTO reportDTO) {
        // Kiểm tra user tồn tại (nếu cần)
        Long userId = reportDTO.getUserId();
        User user = userInfoRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        // Gắn user cho report


        Report report = new Report();
        report.setUser(user);
        report.setReportContent(reportDTO.getReportContent());
        report.setReportDate(reportDTO.getReportDate());
        report.setReportCheck(reportDTO.isReportCheck());
        reportRepository.save(report);

        // Lưu
        return reportDTO;
    }

    @Override
    public Report updateReport(Long id, Report newReportData) {
        Report existingReport = getReportById(id);

        // Cập nhật nội dung
        if (newReportData.getReportContent() != null) {
            existingReport.setReportContent(newReportData.getReportContent());
        }
        // Cập nhật ngày
        if (newReportData.getReportDate() != null) {
            existingReport.setReportDate(newReportData.getReportDate());
        }
        // Cập nhật trạng thái
        existingReport.setReportCheck(newReportData.isReportCheck());

        // Cho phép đổi user nếu muốn
        if (newReportData.getUser() != null) {
            Long userId = newReportData.getUser().getUserId();
            User user = userInfoRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            existingReport.setUser(user);
        }

        return reportRepository.save(existingReport);
    }

    @Override
    public void deleteReport(Long id) {
        Report report = getReportById(id);
        reportRepository.delete(report);
    }
}
