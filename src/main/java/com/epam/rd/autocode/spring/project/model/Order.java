package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private Employee employee;

    @Column
    private LocalDateTime orderDate;

    @Column
    private BigDecimal price;

    @OneToMany
    private List<BookItem> bookItems = new ArrayList<>();
}
