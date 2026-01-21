package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.security.jwt.JwtUtil;
import com.epam.rd.autocode.spring.project.service.ClientService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final ClientService clientService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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


}
