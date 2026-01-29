package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateDTO {
    @NotBlank
    private String name;
    private BigDecimal balance;
    private String phone;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    public EmployeeDTO toEmployeeDTO() {
        return new EmployeeDTO(null, null, name, phone, birthDate);
    }

    public ClientDTO toClientDTO() {
        return new ClientDTO(null, null, name, balance);
    }
}
