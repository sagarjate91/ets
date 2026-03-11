package com.data.ets.service;

import com.data.ets.dto.AttendanceDTO;
import com.data.ets.model.Attendance;
import com.data.ets.model.Employee;
import com.data.ets.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;

    public Attendance checkIn(Long employeeId) {
        Employee employee = new Employee();
        employee.setId(employeeId);
        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setAttendanceDate(LocalDate.now());
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setStatus("PRESENT");
        return attendanceRepository.save(attendance);
    }

    public Attendance checkOut(Long attendanceId) {
        return attendanceRepository.findById(attendanceId).map(attendance -> {
            attendance.setCheckOutTime(LocalDateTime.now());
            return attendanceRepository.save(attendance);
        }).orElseThrow(() -> new RuntimeException("Attendance record not found"));
    }

    public Attendance createAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public Optional<Attendance> findById(Long id) {
        return attendanceRepository.findById(id);
    }

    public List<Attendance> findByEmployeeId(Long employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId);
    }

    public List<Attendance> findByAttendanceDate(LocalDate attendanceDate) {
        return attendanceRepository.findByAttendanceDate(attendanceDate);
    }

    public List<Attendance> findByEmployeeIdAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByEmployeeIdAndAttendanceDateBetween(employeeId, startDate, endDate);
    }

    public List<AttendanceDTO> getAllAttendance() {
        return attendanceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AttendanceDTO> getEmployeeAttendance(Long employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Attendance updateAttendance(Long id, Attendance updatedAttendance) {
        return attendanceRepository.findById(id).map(attendance -> {
            attendance.setStatus(updatedAttendance.getStatus());
            attendance.setRemarks(updatedAttendance.getRemarks());
            return attendanceRepository.save(attendance);
        }).orElseThrow(() -> new RuntimeException("Attendance record not found"));
    }

    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }

    private AttendanceDTO convertToDTO(Attendance attendance) {
        return AttendanceDTO.builder()
                .id(attendance.getId())
                .employeeId(attendance.getEmployee().getId())
                .employeeName(attendance.getEmployee().getUser().getFirstName() + " " + attendance.getEmployee().getUser().getLastName())
                .attendanceDate(attendance.getAttendanceDate())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .status(attendance.getStatus())
                .remarks(attendance.getRemarks())
                .build();
    }
}

