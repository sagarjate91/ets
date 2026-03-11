package com.data.ets.controller;

import com.data.ets.dto.AttendanceDTO;
import com.data.ets.dto.LeaveDTO;
import com.data.ets.model.Attendance;
import com.data.ets.service.AttendanceService;
import com.data.ets.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping
    public String listAttendance(Model model) {
        List<AttendanceDTO> attendanceList = attendanceService.getAllAttendance();
        model.addAttribute("attendanceList", attendanceList);
        return "admin/attendance/list";
    }

    @GetMapping("/add")
    public String addAttendanceForm() {
        return "admin/attendance/add";
    }

    @PostMapping("/save")
    public String saveAttendance(@RequestParam Long employeeId,
                                 @RequestParam LocalDate attendanceDate,
                                 @RequestParam String status,
                                 @RequestParam String remarks) {
        Attendance attendance = Attendance.builder()
                .attendanceDate(attendanceDate)
                .status(status)
                .remarks(remarks)
                .build();
        attendanceService.createAttendance(attendance);
        return "redirect:/admin/attendance";
    }

    @GetMapping("/edit/{id}")
    public String editAttendanceForm(@PathVariable Long id, Model model) {
        Attendance attendance = attendanceService.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));
        model.addAttribute("attendance", attendance);
        return "admin/attendance/edit";
    }

    @PostMapping("/update/{id}")
    public String updateAttendance(@PathVariable Long id,
                                   @RequestParam String status,
                                   @RequestParam String remarks) {
        Attendance attendance = Attendance.builder()
                .status(status)
                .remarks(remarks)
                .build();
        attendanceService.updateAttendance(id, attendance);
        return "redirect:/admin/attendance";
    }

    @GetMapping("/delete/{id}")
    public String deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return "redirect:/admin/attendance";
    }
}

