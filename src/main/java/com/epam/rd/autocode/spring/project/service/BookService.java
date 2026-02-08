package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.filter.BookFilterDTO;
import org.springframework.data.domain.Page;

public interface BookService {

    Page<BookDTO> getAllBooks(BookFilterDTO filter, Boolean isEmployee);

    BookDTO getBookByName(String name);

    BookDTO updateBookByName(String name, BookDTO book);

    void toggleBookActive(String name);

    BookDTO addBook(BookDTO book);
}
