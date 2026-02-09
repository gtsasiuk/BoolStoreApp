package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.model.User;
import com.epam.rd.autocode.spring.project.security.CustomUserDetails;
import com.epam.rd.autocode.spring.project.security.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private AuthServiceImpl authService;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("user@mail.com");

        userDetails = new CustomUserDetails(user, false);
    }

    @Test
    @DisplayName("Authenticate user with valid credentials returns JWT token")
    void authenticateWithValidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(jwtUtil.generateToken("user@mail.com", "ROLE_CUSTOMER"))
                .thenReturn("jwt-token");

        String token = authService.authenticate("user@mail.com", "password");

        assertEquals("jwt-token", token);
        verify(authenticationManager).authenticate(any());
        verify(jwtUtil).generateToken("user@mail.com", "ROLE_CUSTOMER");
    }

    @Test
    @DisplayName("Authenticate user with wrong credentials throws BadCredentialsException")
    void authenticateWithWrongCredentials() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.authenticate("user@mail.com", "wrong-password")
        );

        verify(jwtUtil, never()).generateToken(any(), any());
    }
}

