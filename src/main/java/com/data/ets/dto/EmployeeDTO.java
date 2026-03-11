package com.data.ets.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String employeeId;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private String departmentName;
    private Long departmentId;
    private String position;
    private Double salary;
    private LocalDate dateOfJoining;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String status;
}

