package com.epam.rd.autocode.spring.project.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    @Email(message = "{validation.email}")
    @NotBlank(message = "{validation.required}")
    private String email;

    @NotBlank(message = "{validation.required}")
    private String password;
}
