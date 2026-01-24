package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.UserProfileDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ClientService clientService;
    private final EmployeeService employeeService;

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }


    @GetMapping
    public String profilePage(Model model, Authentication authentication,
                              @RequestParam(value = "edit", required = false) Boolean edit) {
        String email = authentication.getName();
        UserProfileDTO profile;

        if (hasRole("ROLE_EMPLOYEE")) {
            var e = employeeService.getEmployeeByEmail(email);
            profile = new UserProfileDTO(e.getName(), e.getEmail(), null, e.getPhone(), e.getBirthDate());
        } else {
            var c = clientService.getClientByEmail(email);
            profile = new UserProfileDTO(c.getName(), c.getEmail(), c.getBalance(), null, null);
        }

        model.addAttribute("user", profile);
        model.addAttribute("edit", edit != null && edit);
        return "profile";
    }

    @PostMapping
    public String updateProfile(UserProfileDTO form, Authentication authentication) {
        String email = authentication.getName();

        if (hasRole("ROLE_EMPLOYEE")) {
            employeeService.updateEmployeeByEmail(email, form.toEmployeeDTO());
        } else {
            clientService.updateClientByEmail(email, form.toClientDTO());
        }

        return "redirect:/profile";
    }
}
