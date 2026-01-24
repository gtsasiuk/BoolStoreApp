package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
    private BigDecimal balance;
    private String phone;
    private LocalDate birthDate;

    public EmployeeDTO toEmployeeDTO() {
        return new EmployeeDTO(name, email, null, phone, birthDate); // пароль не міняємо тут
    }

    public ClientDTO toClientDTO() {
        return new ClientDTO(name, email, null, balance); // пароль не міняємо тут
    }
}
