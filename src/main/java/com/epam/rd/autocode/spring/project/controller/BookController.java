package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public String booksPage(Model model, Authentication auth) {
        boolean isEmployee = auth != null &&
                auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));

        if (isEmployee) {
            model.addAttribute("books", bookService.getAllBooks());
        } else {
            model.addAttribute("books", bookService.getAllBooksForCustomers());
        }

        return "books/book_list";
    }

    @GetMapping("/{name}")
    public String book(@PathVariable String name, Model model, Authentication auth) {
        BookDTO book = bookService.getBookByName(name);

        boolean isEmployee = auth != null &&
                auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));

        if (!book.getActive() && !isEmployee) {
            throw new AccessDeniedException("Book is not available");
        }

        model.addAttribute("book", book);
        return "books/book_details";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("book", new BookDTO());
        return "books/book_add";
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
        return "books/book_edit";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/edit/{name}")
    public String edit(@PathVariable String name, BookDTO dto, Model model) {
        model.addAttribute("book", bookService.updateBookByName(name, dto));
        return "redirect:/books/{name}";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/toggle-active/{name}")
    public String toggleBookStatus(@PathVariable String name) {
        bookService.toggleBookActive(name);
        return "redirect:/books";
    }
}
