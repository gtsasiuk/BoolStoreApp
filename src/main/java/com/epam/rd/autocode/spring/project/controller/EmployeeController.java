package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public String allClients(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "employees/employee_list";
    }

    @PostMapping("/toggle-block")
    public String blockClient(@RequestParam String email) {
        employeeService.toggleBlockByEmail(email);
        return "redirect:/employees";
    }

    @GetMapping("/new")
    public String createEmployeeAccountPage(Model model) {
        model.addAttribute("employee", new EmployeeDTO());
        return "employees/employee_create";
    }

    @PostMapping("/new")
    public String createEmployeeAccount(@Valid @ModelAttribute EmployeeDTO employee) {
        employeeService.addEmployee(employee);
        return "redirect:/employees";
    }

    @PostMapping("/delete")
    public String createEmployeeAccount(@RequestParam String email) {
        employeeService.deleteEmployeeByEmail(email);
        return "redirect:/employees";
    }
}
