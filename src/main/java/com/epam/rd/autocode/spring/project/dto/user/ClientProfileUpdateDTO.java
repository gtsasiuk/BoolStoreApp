package com.epam.rd.autocode.spring.project.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientProfileUpdateDTO {
    @NotBlank(message = "{validation.required}")
    @Size(min = 3, message = "{validation.name}")
    private String name;

    @NotNull(message = "{validation.required}")
    @PositiveOrZero(message = "{validation.balance}")
    private BigDecimal balance;
}
