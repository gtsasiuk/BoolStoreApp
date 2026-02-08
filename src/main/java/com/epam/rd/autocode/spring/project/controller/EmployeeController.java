package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.filter.UserFilterDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public String allEmployees(@ModelAttribute("filter") UserFilterDTO filter, Model model) {
        Page<EmployeeDTO> employees = employeeService.getAllEmployees(filter);
        model.addAttribute("employees", employees);
        return "employees/employee_list";
    }

    @PostMapping("/toggle-block")
    public String blockEmployee(@ModelAttribute("filter") UserFilterDTO filter, @RequestParam String email) {
        if (email.equals(SecurityContextHolder.getContext()
                .getAuthentication().getName())) {
            throw new IllegalStateException("You cannot block yourself");
        }
        employeeService.toggleBlockByEmail(email);
        return "redirect:/employees?"  + filter.toQueryString();
    }

    @GetMapping("/new")
    public String createEmployeeAccountPage(Model model) {
        model.addAttribute("employee", new EmployeeDTO());
        return "employees/employee_create";
    }

    @PostMapping("/new")
    public String createEmployeeAccount(@Valid @ModelAttribute("employee") EmployeeDTO employee, BindingResult result) {
        if (result.hasErrors()) {
            return "employees/employee_create";
        }

        try {
            employeeService.addEmployee(employee);
            return "redirect:/employees";
        } catch (AlreadyExistException ex) {
            result.rejectValue(
                    "email",
                    "register.alreadyExist",
                    "Account with this email already exists"
            );
            return "employees/employee_create";
        }
    }

}
