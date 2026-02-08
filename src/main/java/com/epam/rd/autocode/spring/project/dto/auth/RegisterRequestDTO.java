package com.epam.rd.autocode.spring.project.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    @NotBlank(message = "{validation.required}")
    @Size(min = 3, message = "{validation.name}")
    private String name;

    @Email(message = "{validation.email}")
    @NotBlank(message = "{validation.required}")
    private String email;

    @NotBlank(message = "{validation.required}")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$",
            message = "{validation.password}"
    )
    private String password;
}
