package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
