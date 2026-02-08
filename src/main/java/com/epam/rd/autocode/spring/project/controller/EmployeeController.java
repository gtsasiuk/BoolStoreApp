package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.filter.UserFilterDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/employees")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public String allEmployees(@ModelAttribute("filter") UserFilterDTO filter, Model model) {
        log.info("Employee list requested");
        Page<EmployeeDTO> employees = employeeService.getAllEmployees(filter);
        model.addAttribute("employees", employees);
        return "employees/employee_list";
    }

    @PostMapping("/toggle-block")
    public String blockEmployee(@ModelAttribute("filter") UserFilterDTO filter, @RequestParam String email) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        log.warn("Toggle block requested for employee email={} by={}", email, currentUser);

        if (email.equals(currentUser)) {
            log.warn("Attempt to block self email={}", email);
            throw new IllegalStateException("You cannot block yourself");
        }
        employeeService.toggleBlockByEmail(email);
        return "redirect:/employees?"  + filter.toQueryString();
    }

    @GetMapping("/new")
    public String createEmployeeAccountPage(Model model) {
        log.info("Opening employee creation page");
        model.addAttribute("employee", new EmployeeDTO());
        return "employees/employee_create";
    }

    @PostMapping("/new")
    public String createEmployeeAccount(@Valid @ModelAttribute("employee") EmployeeDTO employee, BindingResult result) {
        log.info("Creating employee account email={}", employee.getEmail());

        if (result.hasErrors()) {
            log.warn("Employee validation failed email={}", employee.getEmail());
            return "employees/employee_create";
        }

        try {
            employeeService.addEmployee(employee);
            log.info("Employee created successfully email={}", employee.getEmail());
            return "redirect:/employees";
        } catch (AlreadyExistException ex) {
            log.warn("Employee already exists email={}", employee.getEmail());
            result.rejectValue(
                    "email",
                    "register.alreadyExist",
                    "Account with this email already exists"
            );
            return "employees/employee_create";
        }
    }

}
