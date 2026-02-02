package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public String booksPage(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "book_list";
    }

    @GetMapping("/{name}")
    public String book(@PathVariable String name, Model model) {
        model.addAttribute("book", bookService.getBookByName(name));
        return "book_details";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("book", new BookDTO());
        return "book_add";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute BookDTO dto) {
        bookService.addBook(dto);
        return "redirect:/books";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/edit/{name}")
    public String editForm(@PathVariable String name, Model model) {
        model.addAttribute("book", bookService.getBookByName(name));
        model.addAttribute("ageGroups", AgeGroup.values());
        model.addAttribute("languages", Language.values());
        return "book_edit";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/edit/{name}")
    public String edit(@PathVariable String name, BookDTO dto, Model model) {
        model.addAttribute("book", bookService.updateBookByName(name, dto));
        return "redirect:/books/{name}";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/delete/{name}")
    public String delete(@PathVariable String name) {
        bookService.deleteBookByName(name);
        return "redirect:/books";
    }
}
