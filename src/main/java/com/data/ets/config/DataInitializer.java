package com.data.ets.config;

import com.data.ets.model.*;
import com.data.ets.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (userRepository.count() > 0) {
            return;
        }

        // Create Departments
        Department itDept = Department.builder()
                .name("Information Technology")
                .description("IT Department")
                .build();
        itDept = departmentRepository.save(itDept);

        Department hrDept = Department.builder()
                .name("Human Resources")
                .description("HR Department")
                .build();
        hrDept = departmentRepository.save(hrDept);

        Department salesDept = Department.builder()
                .name("Sales")
                .description("Sales Department")
                .build();
        salesDept = departmentRepository.save(salesDept);

        // Create Admin User
        User adminUser = User.builder()
                .username("admin")
                .email("admin@ets.com")
                .password(passwordEncoder.encode("admin123"))
                .firstName("Admin")
                .lastName("User")
                .role(Role.ADMIN)
                .enabled(true)
                .build();
        userRepository.save(adminUser);

        // Create Employee Users
        User emp1User = User.builder()
                .username("emp1")
                .email("emp1@ets.com")
                .password(passwordEncoder.encode("emp123"))
                .firstName("John")
                .lastName("Doe")
                .role(Role.EMPLOYEE)
                .enabled(true)
                .build();
        emp1User = userRepository.save(emp1User);

        User emp2User = User.builder()
                .username("emp2")
                .email("emp2@ets.com")
                .password(passwordEncoder.encode("emp123"))
                .firstName("Jane")
                .lastName("Smith")
                .role(Role.EMPLOYEE)
                .enabled(true)
                .build();
        emp2User = userRepository.save(emp2User);

        User emp3User = User.builder()
                .username("emp3")
                .email("emp3@ets.com")
                .password(passwordEncoder.encode("emp123"))
                .firstName("Mike")
                .lastName("Johnson")
                .role(Role.EMPLOYEE)
                .enabled(true)
                .build();
        emp3User = userRepository.save(emp3User);

        // Create Employees
        Employee emp1 = Employee.builder()
                .user(emp1User)
                .employeeId("EMP001")
                .phoneNumber("9876543210")
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .gender("Male")
                .department(itDept)
                .position("Software Engineer")
                .salary(50000.0)
                .dateOfJoining(LocalDate.of(2020, 1, 15))
                .address("123 Main St")
                .city("New York")
                .state("NY")
                .zipCode("10001")
                .status("ACTIVE")
                .build();
        emp1 = employeeRepository.save(emp1);

        Employee emp2 = Employee.builder()
                .user(emp2User)
                .employeeId("EMP002")
                .phoneNumber("9876543211")
                .dateOfBirth(LocalDate.of(1998, 8, 20))
                .gender("Female")
                .department(hrDept)
                .position("HR Manager")
                .salary(45000.0)
                .dateOfJoining(LocalDate.of(2019, 6, 1))
                .address("456 Oak Ave")
                .city("Los Angeles")
                .state("CA")
                .zipCode("90001")
                .status("ACTIVE")
                .build();
        emp2 = employeeRepository.save(emp2);

        Employee emp3 = Employee.builder()
                .user(emp3User)
                .employeeId("EMP003")
                .phoneNumber("9876543212")
                .dateOfBirth(LocalDate.of(1992, 3, 10))
                .gender("Male")
                .department(salesDept)
                .position("Sales Executive")
                .salary(40000.0)
                .dateOfJoining(LocalDate.of(2021, 3, 15))
                .address("789 Pine Rd")
                .city("Chicago")
                .state("IL")
                .zipCode("60601")
                .status("ACTIVE")
                .build();
        emp3 = employeeRepository.save(emp3);

        // Create Sample Attendance Records
        Attendance att1 = Attendance.builder()
                .employee(emp1)
                .attendanceDate(LocalDate.now())
                .checkInTime(LocalDateTime.now().withHour(9).withMinute(0))
                .checkOutTime(LocalDateTime.now().withHour(17).withMinute(30))
                .status("PRESENT")
                .remarks("Regular")
                .build();
        attendanceRepository.save(att1);

        Attendance att2 = Attendance.builder()
                .employee(emp2)
                .attendanceDate(LocalDate.now())
                .checkInTime(LocalDateTime.now().withHour(9).withMinute(15))
                .checkOutTime(LocalDateTime.now().withHour(17).withMinute(45))
                .status("PRESENT")
                .remarks("Regular")
                .build();
        attendanceRepository.save(att2);

        // Create Sample Leave Requests
        Leave leave1 = Leave.builder()
                .employee(emp1)
                .leaveType("SICK")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .reason("Medical Appointment")
                .status("PENDING")
                .build();
        leaveRepository.save(leave1);

        Leave leave2 = Leave.builder()
                .employee(emp2)
                .leaveType("CASUAL")
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(7))
                .reason("Family vacation")
                .status("APPROVED")
                .approvedBy("Admin")
                .approvalDate(LocalDateTime.now())
                .build();
        leaveRepository.save(leave2);

        System.out.println("Sample data initialized successfully!");
    }
}

