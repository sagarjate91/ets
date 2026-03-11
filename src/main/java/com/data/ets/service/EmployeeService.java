package com.data.ets.service;

import com.data.ets.dto.EmployeeDTO;
import com.data.ets.model.Employee;
import com.data.ets.model.User;
import com.data.ets.repository.EmployeeRepository;
import com.data.ets.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    public Optional<Employee> findByEmployeeId(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId);
    }

    public Optional<Employee> findByUserId(Long userId) {
        return employeeRepository.findByUserId(userId);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public List<Employee> findByDepartmentId(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }

    public List<Employee> findByStatus(String status) {
        return employeeRepository.findByStatus(status);
    }

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EmployeeDTO getEmployeeDTO(Long id) {
        return employeeRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setPhoneNumber(updatedEmployee.getPhoneNumber());
            employee.setDateOfBirth(updatedEmployee.getDateOfBirth());
            employee.setGender(updatedEmployee.getGender());
            employee.setDepartment(updatedEmployee.getDepartment());
            employee.setPosition(updatedEmployee.getPosition());
            employee.setSalary(updatedEmployee.getSalary());
            employee.setAddress(updatedEmployee.getAddress());
            employee.setCity(updatedEmployee.getCity());
            employee.setState(updatedEmployee.getState());
            employee.setZipCode(updatedEmployee.getZipCode());
            employee.setStatus(updatedEmployee.getStatus());
            return employeeRepository.save(employee);
        }).orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public EmployeeDTO convertToDTO(Employee employee) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .username(employee.getUser().getUsername())
                .email(employee.getUser().getEmail())
                .firstName(employee.getUser().getFirstName())
                .lastName(employee.getUser().getLastName())
                .employeeId(employee.getEmployeeId())
                .phoneNumber(employee.getPhoneNumber())
                .dateOfBirth(employee.getDateOfBirth())
                .gender(employee.getGender())
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .position(employee.getPosition())
                .salary(employee.getSalary())
                .dateOfJoining(employee.getDateOfJoining())
                .address(employee.getAddress())
                .city(employee.getCity())
                .state(employee.getState())
                .zipCode(employee.getZipCode())
                .status(employee.getStatus())
                .build();
    }
}

