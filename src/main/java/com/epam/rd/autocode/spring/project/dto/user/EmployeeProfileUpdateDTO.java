package com.epam.rd.autocode.spring.project.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProfileUpdateDTO {
    @NotBlank(message = "{validation.required}")
    @Size(min = 3, message = "{validation.name}")
    private String name;

    @Pattern(
            regexp = "^\\d{3}-\\d{3}-\\d{4}$",
            message = "{validation.phone}"
    )
    @NotBlank(message = "{validation.required}")
    private String phone;

    @NotNull(message = "{validation.required}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
}
