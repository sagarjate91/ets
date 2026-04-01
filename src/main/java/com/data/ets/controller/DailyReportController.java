package com.data.ets.controller;

import com.data.ets.dto.DailyReportDTO;
import com.data.ets.dto.EmployeeDTO;
import com.data.ets.model.DailyReport;
import com.data.ets.model.Employee;
import com.data.ets.service.DailyReportService;
import com.data.ets.service.EmployeeService;
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
@RequestMapping("/admin/daily-reports")
@RequiredArgsConstructor
public class DailyReportController {

    private final DailyReportService dailyReportService;
    private final EmployeeService employeeService;

    @GetMapping
    public String listReports(@RequestParam(required = false) Long employeeId,
                              @RequestParam(required = false) String startDate,
                              @RequestParam(required = false) String endDate,
                              Model model) {
        LocalDate start = (startDate != null && !startDate.isEmpty())
                ? LocalDate.parse(startDate)
                : LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = (endDate != null && !endDate.isEmpty())
                ? LocalDate.parse(endDate)
                : start.plusDays(6);

        List<DailyReportDTO> reports;
        if (employeeId != null) {
            reports = dailyReportService.getEmployeeWeeklyReports(employeeId, start, end);
        } else {
            reports = dailyReportService.getReportsByDateRange(start, end);
        }

        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        model.addAttribute("reports", reports);
        model.addAttribute("employees", employees);
        model.addAttribute("selectedEmployeeId", employeeId);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        return "admin/daily-reports/list";
    }

    @GetMapping("/weekly")
    public String weeklyView(@RequestParam(required = false) Long employeeId,
                             @RequestParam(required = false) String weekDate,
                             Model model) {
        LocalDate monday = (weekDate != null && !weekDate.isEmpty())
                ? LocalDate.parse(weekDate).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                : LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);

        List<DailyReportDTO> reports;
        if (employeeId != null) {
            reports = dailyReportService.getEmployeeWeeklyReports(employeeId, monday, sunday);
        } else {
            reports = dailyReportService.getReportsByDateRange(monday, sunday);
        }

        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        model.addAttribute("reports", reports);
        model.addAttribute("employees", employees);
        model.addAttribute("selectedEmployeeId", employeeId);
        model.addAttribute("weekStart", monday);
        model.addAttribute("weekEnd", sunday);
        model.addAttribute("prevWeek", monday.minusDays(7));
        model.addAttribute("nextWeek", monday.plusDays(7));
        return "admin/daily-reports/weekly";
    }

    @GetMapping("/view/{id}")
    public String viewReport(@PathVariable Long id, Model model) {
        dailyReportService.findById(id).ifPresent(report ->
                model.addAttribute("report", dailyReportService.convertToDTO(report)));
        return "admin/daily-reports/view";
    }

    @GetMapping("/delete/{id}")
    public String deleteReport(@PathVariable Long id) {
        dailyReportService.deleteReport(id);
        return "redirect:/admin/daily-reports";
    }
}
