package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.auth.LoginRequestDTO;
import com.epam.rd.autocode.spring.project.dto.auth.RegisterRequestDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.service.AuthService;
import com.epam.rd.autocode.spring.project.service.ClientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ClientService clientService;
    @MockBean
    private AuthService authService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("GET /auth/login returns login page")
    void loginPage() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("loginForm"));
    }

    @Test
    @DisplayName("GET /auth/register returns register page")
    void registerPage() throws Exception {
        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("registerForm"));
    }

    @Test
    @DisplayName("POST /auth/login with validation error returns login page")
    void loginValidationError() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .param("email", "") // invalid
                        .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @DisplayName("POST /auth/login with wrong credentials returns login page")
    void loginAuthFailed() throws Exception {
        when(authService.authenticate(eq("user@mail.com"), eq("wrong")))
                .thenThrow(new RuntimeException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                        .param("email", "user@mail.com")
                        .param("password", "wrong"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("POST /auth/login success sets cookie and redirects")
    void loginSuccess() throws Exception {
        when(authService.authenticate(eq("user@mail.com"), eq("pass")))
                .thenReturn("jwt-token");

        mockMvc.perform(post("/auth/login")
                        .param("email", "user@mail.com")
                        .param("password", "pass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().exists("JWT"))
                .andExpect(cookie().httpOnly("JWT", true));
    }

    @Test
    @DisplayName("POST /auth/register with validation error")
    void registerValidationError() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .param("email", "")
                        .param("name", "")
                        .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }

    @Test
    @DisplayName("POST /auth/register email already exists")
    void registerAlreadyExists() throws Exception {
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");

        doThrow(new AlreadyExistException("exists"))
                .when(clientService)
                .addClient(any());

        mockMvc.perform(post("/auth/register")
                        .param("email", "user@mail.com")
                        .param("name", "User")
                        .param("password", "pass123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("registerForm", "email"));
    }

    @Test
    @DisplayName("POST /auth/register runtime exception")
    void registerRuntimeException() throws Exception {
        when(passwordEncoder.encode("pass")).thenReturn("encoded");

        doThrow(new RuntimeException("fail"))
                .when(clientService)
                .addClient(any());

        mockMvc.perform(post("/auth/register")
                        .param("email", "user@mail.com")
                        .param("name", "User")
                        .param("password", "pass"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("POST /auth/register success redirects and sets cookie")
    void registerSuccess() throws Exception {
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(authService.authenticate("user@mail.com", "pass123")).thenReturn("jwt-token");

        mockMvc.perform(post("/auth/register")
                        .param("email", "user@mail.com")
                        .param("name", "User")
                        .param("password", "pass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(cookie().exists("JWT"));
    }
}
