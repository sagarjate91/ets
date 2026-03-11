package com.data.ets.repository;

import com.data.ets.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeId(String employeeId);
    Optional<Employee> findByUserId(Long userId);
    List<Employee> findByDepartmentId(Long departmentId);
    List<Employee> findByStatus(String status);
}

