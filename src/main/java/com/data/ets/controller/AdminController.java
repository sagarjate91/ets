package com.data.ets.controller;

import com.data.ets.dto.EmployeeDTO;
import com.data.ets.model.Department;
import com.data.ets.model.Employee;
import com.data.ets.model.Role;
import com.data.ets.model.User;
import com.data.ets.service.DepartmentService;
import com.data.ets.service.EmployeeService;
import com.data.ets.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalEmployees", employeeService.findAll().size());
        model.addAttribute("totalUsers", userService.findAll().size());
        model.addAttribute("totalDepartments", departmentService.findAll().size());
        return "admin/dashboard";
    }

    // Employee Management
    @GetMapping("/employees")
    public String listEmployees(Model model) {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "admin/employees/list";
    }

    @GetMapping("/employees/add")
    public String addEmployeeForm(Model model) {
        List<Department> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        model.addAttribute("roles", Role.values());
        return "admin/employees/add";
    }

    @PostMapping("/employees/save")
    public String saveEmployee(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam String employeeId,
                               @RequestParam String phoneNumber,
                               @RequestParam String position,
                               @RequestParam Double salary,
                               @RequestParam Long departmentId) {
        User user = userService.createUser(username, email, password, firstName, lastName, Role.EMPLOYEE);
        
        Department department = departmentService.findById(departmentId).orElse(null);
        Employee employee = Employee.builder()
                .user(user)
                .employeeId(employeeId)
                .phoneNumber(phoneNumber)
                .position(position)
                .salary(salary)
                .department(department)
                .status("ACTIVE")
                .build();
        
        employeeService.createEmployee(employee);
        return "redirect:/admin/employees";
    }

    @GetMapping("/employees/edit/{id}")
    public String editEmployeeForm(@PathVariable Long id, Model model) {
        EmployeeDTO employee = employeeService.getEmployeeDTO(id);
        List<Department> departments = departmentService.findAll();
        model.addAttribute("employee", employee);
        model.addAttribute("departments", departments);
        return "admin/employees/edit";
    }

    @PostMapping("/employees/update/{id}")
    public String updateEmployee(@PathVariable Long id,
                                 @RequestParam String phoneNumber,
                                 @RequestParam String position,
                                 @RequestParam Double salary,
                                 @RequestParam Long departmentId) {
        Employee employee = employeeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        employee.setPhoneNumber(phoneNumber);
        employee.setPosition(position);
        employee.setSalary(salary);
        employee.setDepartment(departmentService.findById(departmentId).orElse(null));
        
        employeeService.updateEmployee(id, employee);
        return "redirect:/admin/employees";
    }

    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/admin/employees";
    }

    // Department Management
    @GetMapping("/departments")
    public String listDepartments(Model model) {
        List<Department> departments = departmentService.findAll();
        model.addAttribute("departments", departments);
        return "admin/departments/list";
    }

    @GetMapping("/departments/add")
    public String addDepartmentForm() {
        return "admin/departments/add";
    }

    @PostMapping("/departments/save")
    public String saveDepartment(@RequestParam String name,
                                 @RequestParam String description) {
        Department department = Department.builder()
                .name(name)
                .description(description)
                .build();
        departmentService.createDepartment(department);
        return "redirect:/admin/departments";
    }

    @GetMapping("/departments/edit/{id}")
    public String editDepartmentForm(@PathVariable Long id, Model model) {
        Department department = departmentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        model.addAttribute("department", department);
        return "admin/departments/edit";
    }

    @PostMapping("/departments/update/{id}")
    public String updateDepartment(@PathVariable Long id,
                                   @RequestParam String name,
                                   @RequestParam String description) {
        Department department = Department.builder()
                .name(name)
                .description(description)
                .build();
        departmentService.updateDepartment(id, department);
        return "redirect:/admin/departments";
    }

    @GetMapping("/departments/delete/{id}")
    public String deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return "redirect:/admin/departments";
    }

    // User Management
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/users/list";
    }

    @GetMapping("/users/disable/{id}")
    public String disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/enable/{id}")
    public String enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return "redirect:/admin/users";
    }
}

