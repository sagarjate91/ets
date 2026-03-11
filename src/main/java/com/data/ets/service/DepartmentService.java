package com.data.ets.service;

import com.data.ets.dto.DepartmentDTO;
import com.data.ets.model.Department;
import com.data.ets.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public Optional<Department> findById(Long id) {
        return departmentRepository.findById(id);
    }

    public Optional<Department> findByName(String name) {
        return departmentRepository.findByName(name);
    }

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Department updateDepartment(Long id, Department updatedDepartment) {
        return departmentRepository.findById(id).map(department -> {
            department.setName(updatedDepartment.getName());
            department.setDescription(updatedDepartment.getDescription());
            return departmentRepository.save(department);
        }).orElseThrow(() -> new RuntimeException("Department not found"));
    }

    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    private DepartmentDTO convertToDTO(Department department) {
        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .build();
    }
}

