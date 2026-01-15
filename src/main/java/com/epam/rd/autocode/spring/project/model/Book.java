package com.epam.rd.autocode.spring.project.model;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String genre;

    @Column(name = "age_group", nullable = false)
    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    @Column
    private BigDecimal price;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column
    private String author;

    @Column
    private Integer pages;

    @Column
    private String characteristics;

    @Column
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Language language;
}
