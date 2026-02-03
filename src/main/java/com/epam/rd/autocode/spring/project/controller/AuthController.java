package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.auth.LoginRequestDTO;
import com.epam.rd.autocode.spring.project.dto.auth.RegisterRequestDTO;
import com.epam.rd.autocode.spring.project.security.CustomUserDetails;
import com.epam.rd.autocode.spring.project.security.jwt.JwtUtil;
import com.epam.rd.autocode.spring.project.service.AuthService;
import com.epam.rd.autocode.spring.project.service.ClientService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginRequestDTO dto,
                        HttpServletResponse response) {
        String token = authService.authenticate(dto.getEmail(), dto.getPassword());
        addJwtCookie(response, token);
        return "redirect:/";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequestDTO dto,
                           HttpServletResponse response) {
        ClientDTO client = new ClientDTO();
        client.setEmail(dto.getEmail());
        client.setName(dto.getName());
        client.setPassword(passwordEncoder.encode(dto.getPassword()));
        client.setBalance(BigDecimal.ZERO);
        client.setBlocked(false);

        clientService.addClient(client);

        String token = authService.authenticate(dto.getEmail(), dto.getPassword());
        addJwtCookie(response, token);

        return "redirect:/";
    }

}
