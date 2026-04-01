package com.data.ets.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyReportDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private String departmentName;
    private LocalDate reportDate;
    private String workSummary;
    private String tasksCompleted;
    private String tasksInProgress;
    private String challenges;
    private String plansForNextDay;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
