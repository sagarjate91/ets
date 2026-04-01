package com.data.ets.repository;

import com.data.ets.model.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

    List<DailyReport> findByEmployeeId(Long employeeId);

    List<DailyReport> findByEmployeeIdOrderByReportDateDesc(Long employeeId);

    List<DailyReport> findByReportDate(LocalDate reportDate);

    List<DailyReport> findByEmployeeIdAndReportDateBetweenOrderByReportDateDesc(
            Long employeeId, LocalDate startDate, LocalDate endDate);

    Optional<DailyReport> findByEmployeeIdAndReportDate(Long employeeId, LocalDate reportDate);

    List<DailyReport> findByReportDateBetweenOrderByReportDateDesc(LocalDate startDate, LocalDate endDate);
}
