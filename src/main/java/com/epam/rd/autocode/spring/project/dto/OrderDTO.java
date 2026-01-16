package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    @NotBlank
    private String clientEmail;
    @NotBlank
    private String employeeEmail;
    @NotNull
    private LocalDateTime orderDate;
    @NotNull
    @Positive
    private BigDecimal price;
    @Valid
    @NotNull
    private List<BookItemDTO> bookItems;
}
