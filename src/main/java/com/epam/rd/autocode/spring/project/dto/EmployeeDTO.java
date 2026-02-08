package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    @Email(message = "{validation.email}")
    @NotBlank(message = "{validation.required}")
    private String email;

    @NotBlank(message = "{validation.required}")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$",
            message = "{validation.password}"
    )
    private String password;
    @NotBlank(message = "{validation.required}")
    @Size(min = 3, message = "{validation.name}")
    private String name;
    @Pattern(
            regexp = "^\\d{3}-\\d{3}-\\d{4}$",
            message = "{validation.phone}"
    )
    private String phone;
    @NotNull(message = "{validation.required}")
    private LocalDate birthDate;
    private Boolean blocked;
}
