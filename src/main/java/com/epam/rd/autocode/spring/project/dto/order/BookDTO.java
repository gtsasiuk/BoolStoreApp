package com.epam.rd.autocode.spring.project.dto.order;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
    @NotBlank(message = "{validation.required}")
    @Size(min = 3, message = "{validation.book.name}")
    private String name;

    @NotBlank(message = "{validation.required}")
    @Size(min = 3, message = "{validation.name}")
    private String author;

    @NotBlank(message = "{validation.required}")
    private String genre;

    @NotNull(message = "{validation.required}")
    private AgeGroup ageGroup;

    @NotNull(message = "{validation.required}")
    @Positive(message = "{validation.positive}")
    private BigDecimal price;

    @NotNull(message = "{validation.required}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate publicationDate;

    @NotNull(message = "{validation.required}")
    @Positive(message = "{validation.positive}")
    private Integer pages;

    @NotNull(message = "{validation.required}")
    private Language language;

    private String characteristics;
    private String description;
    private Boolean active;
}
