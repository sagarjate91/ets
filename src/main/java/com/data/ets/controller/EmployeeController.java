package com.data.ets.controller;

import com.data.ets.dto.AttendanceDTO;
import com.data.ets.dto.DailyReportDTO;
import com.data.ets.dto.EmployeeDTO;
import com.data.ets.dto.LeaveDTO;
import com.data.ets.model.Attendance;
import com.data.ets.model.DailyReport;
import com.data.ets.model.Employee;
import com.data.ets.model.Leave;
import com.data.ets.service.AttendanceService;
import com.data.ets.service.DailyReportService;
import com.data.ets.service.EmployeeService;
import com.data.ets.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Controller
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final LeaveService leaveService;
    private final DailyReportService dailyReportService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        Employee employee = employeeService.findAll().stream()
                .filter(e -> e.getUser().getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (employee != null) {
            model.addAttribute("employee", employeeService.convertToDTO(employee));
            model.addAttribute("recentAttendance", attendanceService.getEmployeeAttendance(employee.getId()));
            model.addAttribute("pendingLeaves", leaveService.getEmployeeLeaves(employee.getId()).stream()
                    .filter(l -> l.getStatus().equals("PENDING"))
                    .toList());
        }
        
        return "employee/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        String username = authentication.getName();
        Employee employee = employeeService.findAll().stream()
                .filter(e -> e.getUser().getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (employee != null) {
            model.addAttribute("employee", employeeService.convertToDTO(employee));
        }
        
        return "employee/profile";
    }

    // Attendance
    @GetMapping("/attendance")
    public String listAttendance(Authentication authentication, Model model) {
        String username = authentication.getName();
        Employee employee = employeeService.findAll().stream()
                .filter(e -> e.getUser().getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (employee != null) {
            List<AttendanceDTO> attendanceList = attendanceService.getEmployeeAttendance(employee.getId());
            model.addAttribute("attendanceList", attendanceList);
            model.addAttribute("totalPresent", attendanceList.stream().filter(a -> a.getStatus().equals("PRESENT")).count());
            model.addAttribute("totalAbsent", attendanceList.stream().filter(a -> a.getStatus().equals("ABSENT")).count());
            model.addAttribute("totalLate", attendanceList.stream().filter(a -> a.getStatus().equals("LATE")).count());
        }
        
        return "employee/attendance";
    }

    @PostMapping("/attendance/check-in")
    public String checkIn(Authentication authentication) {
        String username = authentication.getName();
        Employee employee = employeeService.findAll().stream()
                .filter(e -> e.getUser().getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (employee != null) {
            attendanceService.checkIn(employee.getId());
        }
        
        return "redirect:/employee/attendance";
    }

    @PostMapping("/attendance/check-out/{attendanceId}")
    public String checkOut(@PathVariable Long attendanceId) {
        attendanceService.checkOut(attendanceId);
        return "redirect:/employee/attendance";
    }

    // Leave Management
    @GetMapping("/leaves")
    public String listLeaves(Authentication authentication, Model model) {
        String username = authentication.getName();
        Employee employee = employeeService.findAll().stream()
                .filter(e -> e.getUser().getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (employee != null) {
            List<LeaveDTO> leaves = leaveService.getEmployeeLeaves(employee.getId());
            model.addAttribute("leaves", leaves);
            model.addAttribute("approved", leaves.stream().filter(l -> l.getStatus().equals("APPROVED")).count());
            model.addAttribute("pending", leaves.stream().filter(l -> l.getStatus().equals("PENDING")).count());
            model.addAttribute("rejected", leaves.stream().filter(l -> l.getStatus().equals("REJECTED")).count());
        }
        
        return "employee/leaves";
    }

    @GetMapping("/leaves/apply")
    public String applyLeaveForm() {
        return "employee/apply-leave";
    }

    @PostMapping("/leaves/save")
    public String applyLeave(Authentication authentication,
                            @RequestParam String leaveType,
                            @RequestParam LocalDate startDate,
                            @RequestParam LocalDate endDate,
                            @RequestParam String reason) {
        String username = authentication.getName();
        Employee employee = employeeService.findAll().stream()
                .filter(e -> e.getUser().getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (employee != null) {
            Leave leave = Leave.builder()
                    .employee(employee)
                    .leaveType(leaveType)
                    .startDate(startDate)
                    .endDate(endDate)
                    .reason(reason)
                    .status("PENDING")
                    .build();
            leaveService.createLeave(leave);
        }
        
        return "redirect:/employee/leaves";
    }

    // Daily Report Diary
    @GetMapping("/daily-reports")
    public String listDailyReports(Authentication authentication,
                                   @RequestParam(required = false) String weekDate,
                                   Model model) {
        String username = authentication.getName();
        Employee employee = employeeService.findAll().stream()
                .filter(e -> e.getUser().getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (employee != null) {
            LocalDate monday = (weekDate != null && !weekDate.isEmpty())
                    ? LocalDate.parse(weekDate).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    : LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate sunday = monday.plusDays(6);

            List<DailyReportDTO> weeklyReports = dailyReportService
                    .getEmployeeWeeklyReports(employee.getId(), monday, sunday);
            List<DailyReportDTO> allReports = dailyReportService.getEmployeeReports(employee.getId());

            model.addAttribute("weeklyReports", weeklyReports);
            model.addAttribute("totalReports", allReports.size());
            model.addAttribute("weekStart", monday);
            model.addAttribute("weekEnd", sunday);
            model.addAttribute("prevWeek", monday.minusDays(7));
            model.addAttribute("nextWeek", monday.plusDays(7));
            model.addAttribute("today", LocalDate.now());
        }

        return "employee/daily-report";
    }

    @GetMapping("/daily-reports/add")
    public String addDailyReportForm(Authentication authentication,
                                     @RequestParam(required = false) String date,
                                     Model model) {
        String username = authentication.getName();
        Employee employee = employeeService.findAll().stream()
                .filter(e -> e.getUser().getUsername().equals(username))
                .findFirst()
                .orElse(null);

        LocalDate reportDate = (date != null && !date.isEmpty())
                ? LocalDate.parse(date)
                : LocalDate.now();

        if (employee != null) {
            // Check if report already exists for this date
            dailyReportService.findByEmployeeIdAndDate(employee.getId(), reportDate)
                    .ifPresent(report -> model.addAttribute("existingReport", dailyReportService.convertToDTO(report)));
        }

        model.addAttribute("reportDate", reportDate);
        return "employee/add-daily-report";
    }

    @PostMapping("/daily-reports/save")
    public String saveDailyReport(Authentication authentication,
                                  @RequestParam LocalDate reportDate,
                                  @RequestParam(required = false) String workSummary,
                                  @RequestParam(required = false) String tasksCompleted,
                                  @RequestParam(required = false) String tasksInProgress,
                                  @RequestParam(required = false) String challenges,
                                  @RequestParam(required = false) String plansForNextDay,
                                  @RequestParam String status) {
        String username = authentication.getName();
        Employee employee = employeeService.findAll().stream()
                .filter(e -> e.getUser().getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (employee != null) {
            // Check if a report already exists for this date and update it
            dailyReportService.findByEmployeeIdAndDate(employee.getId(), reportDate)
                    .ifPresentOrElse(
                            existing -> {
                                existing.setWorkSummary(workSummary);
                                existing.setTasksCompleted(tasksCompleted);
                                existing.setTasksInProgress(tasksInProgress);
                                existing.setChallenges(challenges);
                                existing.setPlansForNextDay(plansForNextDay);
                                existing.setStatus(status);
                                dailyReportService.updateReport(existing.getId(), existing);
                            },
                            () -> {
                                DailyReport report = DailyReport.builder()
                                        .employee(employee)
                                        .reportDate(reportDate)
                                        .workSummary(workSummary)
                                        .tasksCompleted(tasksCompleted)
                                        .tasksInProgress(tasksInProgress)
                                        .challenges(challenges)
                                        .plansForNextDay(plansForNextDay)
                                        .status(status)
                                        .build();
                                dailyReportService.createReport(report);
                            });
        }

        return "redirect:/employee/daily-reports";
    }

    @GetMapping("/daily-reports/edit/{id}")
    public String editDailyReportForm(@PathVariable Long id, Model model) {
        dailyReportService.findById(id).ifPresent(report ->
                model.addAttribute("existingReport", dailyReportService.convertToDTO(report)));
        return "employee/add-daily-report";
    }

    @PostMapping("/daily-reports/update/{id}")
    public String updateDailyReport(@PathVariable Long id,
                                    @RequestParam LocalDate reportDate,
                                    @RequestParam(required = false) String workSummary,
                                    @RequestParam(required = false) String tasksCompleted,
                                    @RequestParam(required = false) String tasksInProgress,
                                    @RequestParam(required = false) String challenges,
                                    @RequestParam(required = false) String plansForNextDay,
                                    @RequestParam String status) {
        dailyReportService.findById(id).ifPresent(report -> {
            report.setReportDate(reportDate);
            report.setWorkSummary(workSummary);
            report.setTasksCompleted(tasksCompleted);
            report.setTasksInProgress(tasksInProgress);
            report.setChallenges(challenges);
            report.setPlansForNextDay(plansForNextDay);
            report.setStatus(status);
            dailyReportService.updateReport(id, report);
        });
        return "redirect:/employee/daily-reports";
    }

    @GetMapping("/daily-reports/delete/{id}")
    public String deleteDailyReport(@PathVariable Long id) {
        dailyReportService.deleteReport(id);
        return "redirect:/employee/daily-reports";
    }
}

