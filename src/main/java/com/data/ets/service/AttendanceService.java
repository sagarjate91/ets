package com.data.ets.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.data.ets.dto.AttendanceDTO;
import com.data.ets.model.Attendance;
import com.data.ets.model.Employee;
import com.data.ets.repository.AttendanceRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;

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

    public byte[] exportToExcel() {
        List<AttendanceDTO> list = getAllAttendance();
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Attendance");

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] columns = {"ID", "Employee", "Date", "Check In", "Check Out", "Status", "Remarks"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (AttendanceDTO a : list) {
                Row row = sheet.createRow(rowIdx++);
                Long attendanceId = a.getId();
                row.createCell(0).setCellValue(attendanceId != null ? attendanceId.doubleValue() : 0d);
                row.createCell(1).setCellValue(a.getEmployeeName() != null ? a.getEmployeeName() : "");
                row.createCell(2).setCellValue(a.getAttendanceDate() != null ? a.getAttendanceDate().toString() : "");
                row.createCell(3).setCellValue(a.getCheckInTime() != null ? a.getCheckInTime().toString() : "");
                row.createCell(4).setCellValue(a.getCheckOutTime() != null ? a.getCheckOutTime().toString() : "");
                row.createCell(5).setCellValue(a.getStatus() != null ? a.getStatus() : "");
                row.createCell(6).setCellValue(a.getRemarks() != null ? a.getRemarks() : "");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export attendance to Excel", e);
        }
    }

    public byte[] exportToPdf() {
        List<AttendanceDTO> list = getAllAttendance();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            try (Document document = new Document(PageSize.A4.rotate())) {
                PdfWriter.getInstance(document, out);
                document.open();

                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.DARK_GRAY);
                Paragraph title = new Paragraph("Attendance Report", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(12f);
                document.add(title);

                PdfPTable table = new PdfPTable(7);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{1f, 2.5f, 1.8f, 2f, 2f, 1.5f, 2f});

                String[] headers = {"ID", "Employee", "Date", "Check In", "Check Out", "Status", "Remarks"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
                    cell.setBackgroundColor(new Color(44, 62, 80));
                    cell.setPadding(6f);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }

                Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
                boolean alternate = false;
                for (AttendanceDTO a : list) {
                    Color rowColor = alternate ? new Color(236, 240, 241) : Color.WHITE;
                    String[] values = {
                        String.valueOf(a.getId()),
                        a.getEmployeeName() != null ? a.getEmployeeName() : "",
                        a.getAttendanceDate() != null ? a.getAttendanceDate().toString() : "",
                        a.getCheckInTime() != null ? a.getCheckInTime().toString() : "",
                        a.getCheckOutTime() != null ? a.getCheckOutTime().toString() : "",
                        a.getStatus() != null ? a.getStatus() : "",
                        a.getRemarks() != null ? a.getRemarks() : ""
                    };
                    for (String v : values) {
                        PdfPCell cell = new PdfPCell(new Phrase(v, bodyFont));
                        cell.setBackgroundColor(rowColor);
                        cell.setPadding(5f);
                        table.addCell(cell);
                    }
                    alternate = !alternate;
                }

                document.add(table);
                return out.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to export attendance to PDF", e);
        }
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

