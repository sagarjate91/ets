package com.data.ets.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.data.ets.dto.AttendanceDTO;
import com.data.ets.model.Attendance;
import com.data.ets.service.AttendanceService;

import lombok.RequiredArgsConstructor;

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

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() {
        byte[] data = attendanceService.exportToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf() {
        byte[] data = attendanceService.exportToPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}

