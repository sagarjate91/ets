package com.data.ets.service;

import com.data.ets.dto.LeaveDTO;
import com.data.ets.model.Leave;
import com.data.ets.repository.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {
    private final LeaveRepository leaveRepository;

    public Leave createLeave(Leave leave) {
        leave.setStatus("PENDING");
        return leaveRepository.save(leave);
    }

    public Optional<Leave> findById(Long id) {
        return leaveRepository.findById(id);
    }

    public List<Leave> findByEmployeeId(Long employeeId) {
        return leaveRepository.findByEmployeeId(employeeId);
    }

    public List<Leave> findByStatus(String status) {
        return leaveRepository.findByStatus(status);
    }

    public List<Leave> findPendingLeaves() {
        return leaveRepository.findByStatus("PENDING");
    }

    public List<Leave> findByEmployeeIdAndStatus(Long employeeId, String status) {
        return leaveRepository.findByEmployeeIdAndStatus(employeeId, status);
    }

    public List<LeaveDTO> getAllLeaves() {
        return leaveRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveDTO> getEmployeeLeaves(Long employeeId) {
        return leaveRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveDTO> getPendingLeaves() {
        return leaveRepository.findByStatus("PENDING").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Leave approveLeave(Long id, String approvedBy) {
        return leaveRepository.findById(id).map(leave -> {
            leave.setStatus("APPROVED");
            leave.setApprovedBy(approvedBy);
            leave.setApprovalDate(LocalDateTime.now());
            return leaveRepository.save(leave);
        }).orElseThrow(() -> new RuntimeException("Leave request not found"));
    }

    public Leave rejectLeave(Long id, String approvedBy) {
        return leaveRepository.findById(id).map(leave -> {
            leave.setStatus("REJECTED");
            leave.setApprovedBy(approvedBy);
            leave.setApprovalDate(LocalDateTime.now());
            return leaveRepository.save(leave);
        }).orElseThrow(() -> new RuntimeException("Leave request not found"));
    }

    public Leave updateLeave(Long id, Leave updatedLeave) {
        return leaveRepository.findById(id).map(leave -> {
            leave.setLeaveType(updatedLeave.getLeaveType());
            leave.setStartDate(updatedLeave.getStartDate());
            leave.setEndDate(updatedLeave.getEndDate());
            leave.setReason(updatedLeave.getReason());
            return leaveRepository.save(leave);
        }).orElseThrow(() -> new RuntimeException("Leave request not found"));
    }

    public void deleteLeave(Long id) {
        leaveRepository.deleteById(id);
    }

    private LeaveDTO convertToDTO(Leave leave) {
        return LeaveDTO.builder()
                .id(leave.getId())
                .employeeId(leave.getEmployee().getId())
                .employeeName(leave.getEmployee().getUser().getFirstName() + " " + leave.getEmployee().getUser().getLastName())
                .leaveType(leave.getLeaveType())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .approvedBy(leave.getApprovedBy())
                .approvalDate(leave.getApprovalDate())
                .build();
    }
}

