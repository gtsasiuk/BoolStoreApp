package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.filter.BookFilterDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public String booksPage(@ModelAttribute("filter") BookFilterDTO filter, Model model, Authentication auth) {
        boolean isEmployee = auth != null &&
                auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));

        log.info("Books page requested isEmployee={} filter={}", isEmployee, filter);

        Page<BookDTO> books = bookService.getAllBooks(filter, isEmployee);

        model.addAttribute("books", books);

        return "books/book_list";
    }

    @GetMapping("/{name}")
    public String book(@PathVariable String name, Model model, Authentication auth) {
        log.info("Book details requested name={}", name);

        BookDTO book = bookService.getBookByName(name);

        boolean isEmployee = auth != null &&
                auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"));

        if (!book.getActive() && !isEmployee) {
            log.warn("Access denied to inactive book name={}", name);
            throw new AccessDeniedException("Book is not available");
        }

        model.addAttribute("book", book);
        return "books/book_details";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/add")
    public String addForm(Model model) {
        log.info("Opening book add form");
        if (!model.containsAttribute("book")) {
            model.addAttribute("book", new BookDTO());
        }
        model.addAttribute("ageGroups", AgeGroup.values());
        model.addAttribute("languages", Language.values());
        return "books/book_add";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("book") BookDTO dto,
                      BindingResult result, Model model) {
        log.info("Attempt to add book name={}", dto.getName());
        if (result.hasErrors()) {
            log.warn("Book validation failed name={}", dto.getName());
            model.addAttribute("ageGroups", AgeGroup.values());
            model.addAttribute("languages", Language.values());
            return "books/book_add";
        }

        try {
            bookService.addBook(dto);
            log.info("Book added successfully name={}", dto.getName());
        }
        catch (AlreadyExistException ex) {
            log.warn("Book already exists name={}", dto.getName());
            result.rejectValue("name", "book.alreadyExist", "Book with this name already exists");
            return "books/book_add";
        }
        return "redirect:/books";
    }


    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/edit/{name}")
    public String editForm(@PathVariable String name, Model model) {
        log.info("Opening edit form for book name={}", name);
        if (!model.containsAttribute("book")) {
            model.addAttribute("book", bookService.getBookByName(name));
        }
        model.addAttribute("ageGroups", AgeGroup.values());
        model.addAttribute("languages", Language.values());
        return "books/book_edit";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/edit/{name}")
    public String edit(@PathVariable String name, @Valid @ModelAttribute("book") BookDTO dto,
                       BindingResult result, Model model) {
        log.info("Attempt to update book name={}", name);
        if (result.hasErrors()) {
            log.warn("Book edit validation failed name={}", name);
            model.addAttribute("ageGroups", AgeGroup.values());
            model.addAttribute("languages", Language.values());
            return "books/book_edit";
        }

        bookService.updateBookByName(name, dto);
        log.info("Book updated successfully name={}", name);
        return "redirect:/books/{name}";
    }


    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/toggle-active/{name}")
    public String toggleBookStatus(@ModelAttribute("filter") BookFilterDTO filter, @PathVariable String name) {
        log.warn("Toggle book active status requested name={}", name);
        bookService.toggleBookActive(name);
        return "redirect:/books?" + filter.toQueryString();
    }
}
