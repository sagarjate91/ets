package com.data.ets.service;

import com.data.ets.dto.DailyReportDTO;
import com.data.ets.model.DailyReport;
import com.data.ets.repository.DailyReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyReportService {

    private final DailyReportRepository dailyReportRepository;

    public DailyReport createReport(DailyReport report) {
        return dailyReportRepository.save(report);
    }

    public DailyReport updateReport(Long id, DailyReport updatedReport) {
        return dailyReportRepository.findById(id).map(report -> {
            report.setWorkSummary(updatedReport.getWorkSummary());
            report.setTasksCompleted(updatedReport.getTasksCompleted());
            report.setTasksInProgress(updatedReport.getTasksInProgress());
            report.setChallenges(updatedReport.getChallenges());
            report.setPlansForNextDay(updatedReport.getPlansForNextDay());
            report.setStatus(updatedReport.getStatus());
            return dailyReportRepository.save(report);
        }).orElseThrow(() -> new RuntimeException("Daily report not found with id: " + id));
    }

    public Optional<DailyReport> findById(Long id) {
        return dailyReportRepository.findById(id);
    }

    public Optional<DailyReport> findByEmployeeIdAndDate(Long employeeId, LocalDate reportDate) {
        return dailyReportRepository.findByEmployeeIdAndReportDate(employeeId, reportDate);
    }

    public List<DailyReport> findByEmployeeId(Long employeeId) {
        return dailyReportRepository.findByEmployeeIdOrderByReportDateDesc(employeeId);
    }

    public List<DailyReport> findByEmployeeIdAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return dailyReportRepository.findByEmployeeIdAndReportDateBetweenOrderByReportDateDesc(
                employeeId, startDate, endDate);
    }

    public List<DailyReport> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return dailyReportRepository.findByReportDateBetweenOrderByReportDateDesc(startDate, endDate);
    }

    public List<DailyReportDTO> getAllReports() {
        return dailyReportRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DailyReportDTO> getEmployeeReports(Long employeeId) {
        return dailyReportRepository.findByEmployeeIdOrderByReportDateDesc(employeeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DailyReportDTO> getEmployeeWeeklyReports(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return dailyReportRepository
                .findByEmployeeIdAndReportDateBetweenOrderByReportDateDesc(employeeId, startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DailyReportDTO> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return dailyReportRepository.findByReportDateBetweenOrderByReportDateDesc(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteReport(Long id) {
        dailyReportRepository.deleteById(id);
    }

    public DailyReportDTO convertToDTO(DailyReport report) {
        String deptName = (report.getEmployee().getDepartment() != null)
                ? report.getEmployee().getDepartment().getName()
                : "N/A";
        return DailyReportDTO.builder()
                .id(report.getId())
                .employeeId(report.getEmployee().getId())
                .employeeName(report.getEmployee().getUser().getFirstName() + " "
                        + report.getEmployee().getUser().getLastName())
                .employeeCode(report.getEmployee().getEmployeeId())
                .departmentName(deptName)
                .reportDate(report.getReportDate())
                .workSummary(report.getWorkSummary())
                .tasksCompleted(report.getTasksCompleted())
                .tasksInProgress(report.getTasksInProgress())
                .challenges(report.getChallenges())
                .plansForNextDay(report.getPlansForNextDay())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}
