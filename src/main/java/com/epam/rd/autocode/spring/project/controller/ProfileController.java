package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.user.*;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ClientService clientService;
    private final EmployeeService employeeService;

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }

    @GetMapping
    public String profilePage(Model model, Authentication auth,
                              @RequestParam(value = "edit", required = false) Boolean edit) {
        String email = auth.getName();

        if (hasRole(auth, "ROLE_EMPLOYEE")) {
            var e = employeeService.getEmployeeByEmail(email);
            model.addAttribute("user", new UserProfileViewDTO(
                    e.getName(), e.getEmail(), null,
                    e.getPhone(), e.getBirthDate(), e.getBlocked()
            ));
            model.addAttribute("employeeForm",
                    new EmployeeProfileUpdateDTO(e.getName(), e.getPhone(), e.getBirthDate()));
        } else {
            var c = clientService.getClientByEmail(email);
            model.addAttribute("user", new UserProfileViewDTO(
                    c.getName(), c.getEmail(), c.getBalance(),
                    null, null, c.getBlocked()
            ));
            model.addAttribute("clientForm",
                    new ClientProfileUpdateDTO(c.getName(), c.getBalance()));
        }

        model.addAttribute("edit", Boolean.TRUE.equals(edit));
        return "profile";
    }


    @PostMapping
    public String updateProfile(
            Authentication auth,
            Model model,

            @Valid @ModelAttribute(value = "employeeForm")
            EmployeeProfileUpdateDTO employeeForm,
            BindingResult employeeErrors,

            @Valid @ModelAttribute(value = "clientForm")
            ClientProfileUpdateDTO clientForm,
            BindingResult clientErrors
    ) {
        String email = auth.getName();

        if (hasRole(auth, "ROLE_EMPLOYEE")) {
            var e = employeeService.getEmployeeByEmail(email);

            if (employeeErrors.hasErrors()) {
                model.addAttribute("user", new UserProfileViewDTO(
                        e.getName(), e.getEmail(), null,
                        e.getPhone(), e.getBirthDate(), e.getBlocked()
                ));
                model.addAttribute("employeeForm", employeeForm);
                model.addAttribute("clientForm", null);
                model.addAttribute("edit", true);
                return "profile";
            }

            employeeService.updateEmployeeByEmail(
                    email,
                    new EmployeeDTO(
                            e.getEmail(),
                            e.getPassword(),
                            employeeForm.getName(),
                            employeeForm.getPhone(),
                            employeeForm.getBirthDate(),
                            e.getBlocked()
                    )
            );

        } else {
            var c = clientService.getClientByEmail(email);

            if (clientErrors.hasErrors()) {
                model.addAttribute("user", new UserProfileViewDTO(
                        c.getName(), c.getEmail(), c.getBalance(),
                        null, null, c.getBlocked()
                ));
                model.addAttribute("clientForm", clientForm);
                model.addAttribute("employeeForm", null);
                model.addAttribute("edit", true);
                return "profile";
            }
            clientService.updateClientByEmail(
                    email,
                    new ClientDTO(
                            null, null,
                            clientForm.getName(),
                            clientForm.getBalance(),
                            null
                    )
            );
        }
        return "redirect:/profile";
    }

    @PostMapping("/delete")
    public String deleteProfile(Authentication auth) {
        clientService.deleteClientByEmail(auth.getName());
        SecurityContextHolder.clearContext();
        return "redirect:/logout";
    }
}
