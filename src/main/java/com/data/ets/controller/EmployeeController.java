package com.data.ets.controller;

import com.data.ets.dto.AttendanceDTO;
import com.data.ets.dto.EmployeeDTO;
import com.data.ets.dto.LeaveDTO;
import com.data.ets.model.Attendance;
import com.data.ets.model.Employee;
import com.data.ets.model.Leave;
import com.data.ets.service.AttendanceService;
import com.data.ets.service.EmployeeService;
import com.data.ets.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final LeaveService leaveService;

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
}

