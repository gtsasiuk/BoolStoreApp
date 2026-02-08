package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.auth.LoginRequestDTO;
import com.epam.rd.autocode.spring.project.dto.auth.RegisterRequestDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.service.AuthService;
import com.epam.rd.autocode.spring.project.service.ClientService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final ClientService clientService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    private void addJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        log.debug("Opening login page");
        model.addAttribute("loginForm", new LoginRequestDTO());
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        log.debug("Opening register page");
        model.addAttribute("registerForm", new RegisterRequestDTO());
        return "auth/register";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginRequestDTO dto,
                        BindingResult result,
                        HttpServletResponse response) {
        log.info("Login attempt for email={}", dto.getEmail());

        if (result.hasErrors()) {
            log.warn("Login validation failed for email={}", dto.getEmail());
            return "auth/login";
        }

        try {
            String token = authService.authenticate(dto.getEmail(), dto.getPassword());
            addJwtCookie(response, token);
            log.info("Login successful for email={}", dto.getEmail());
            return "redirect:/";
        } catch (RuntimeException ex) {
            log.warn("Login failed for email={}", dto.getEmail());
            result.reject("login.failed", "Invalid email or password");
            return "auth/login";
        }
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterRequestDTO dto,
                           BindingResult result,
                           HttpServletResponse response) {
        log.info("Registration attempt for email={}", dto.getEmail());

        if (result.hasErrors()) {
            log.warn("Registration validation failed for email={}", dto.getEmail());
            return "auth/register";
        }

        try {
            ClientDTO client = new ClientDTO();
            client.setEmail(dto.getEmail());
            client.setName(dto.getName());
            client.setPassword(passwordEncoder.encode(dto.getPassword()));
            client.setBalance(BigDecimal.ZERO);
            client.setBlocked(false);

            clientService.addClient(client);

            log.info("Client registered successfully email={}", dto.getEmail());

            String token = authService.authenticate(dto.getEmail(), dto.getPassword());
            addJwtCookie(response, token);

            return "redirect:/";
        } catch (AlreadyExistException ex) {
            log.warn("Registration failed: email already exists email={}", dto.getEmail());
            result.rejectValue("email", "register.alreadyExist", "Account with this email already exists");
            return "auth/register";
        } catch (RuntimeException ex) {
            log.error("Unexpected error during registration email={}", dto.getEmail(), ex);
            result.reject("register.failed", "Registration failed. Please try again.");
            return "auth/register";
        }
    }

}
