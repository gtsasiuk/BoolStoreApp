package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.filter.BookFilterDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @Mock
    private BookRepository repository;
    @Mock
    private ModelMapper mapper;
    @InjectMocks
    private BookServiceImpl service;


    @Test
    @DisplayName("Add book with unique name succeeds")
    void addBookSuccess() {
        BookDTO dto = new BookDTO();
        dto.setName("Book");

        Book entity = new Book();

        when(repository.existsByName("Book")).thenReturn(false);
        when(mapper.map(dto, Book.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.map(entity, BookDTO.class)).thenReturn(dto);

        BookDTO result = service.addBook(dto);

        assertEquals("Book", result.getName());
        assertTrue(entity.getActive());
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Add book with existing name throws AlreadyExistException")
    void addBookAlreadyExists() {
        BookDTO dto = new BookDTO();
        dto.setName("Book");

        when(repository.existsByName("Book")).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> service.addBook(dto));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Get book by name returns DTO")
    void getBookByNameSuccess() {
        Book book = new Book();
        BookDTO dto = new BookDTO();

        when(repository.findByName("Book")).thenReturn(Optional.of(book));
        when(mapper.map(book, BookDTO.class)).thenReturn(dto);

        BookDTO result = service.getBookByName("Book");

        assertNotNull(result);
    }

    @Test
    @DisplayName("Get book by name throws NotFoundException")
    void getBookByNameNotFound() {
        when(repository.findByName("Book")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.getBookByName("Book"));
    }

    @Test
    @DisplayName("Update book by name updates fields")
    void updateBookSuccess() {
        Book book = new Book();
        BookDTO dto = new BookDTO();
        dto.setName("New Book");

        when(repository.findByName("Book")).thenReturn(Optional.of(book));
        when(repository.save(book)).thenReturn(book);
        when(mapper.map(book, BookDTO.class)).thenReturn(dto);

        BookDTO result = service.updateBookByName("Book", dto);

        assertEquals("New Book", result.getName());
        verify(repository).save(book);
    }

    @Test
    @DisplayName("Update book by name throws NotFoundException")
    void updateBookNotFound() {
        when(repository.findByName("Book")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.updateBookByName("Book", new BookDTO()));
    }

    @Test
    @DisplayName("Toggle book active switches active flag")
    void toggleBookActiveSuccess() {
        Book book = new Book();
        book.setActive(true);

        when(repository.findByName("Book")).thenReturn(Optional.of(book));

        service.toggleBookActive("Book");

        assertFalse(book.getActive());
        verify(repository).save(book);
    }

    @Test
    @DisplayName("Toggle book active throws NotFoundException")
    void toggleBookActiveNotFound() {
        when(repository.findByName("Book")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.toggleBookActive("Book"));
    }

    @Test
    @DisplayName("Get all books for client returns only active books")
    void getAllBooksClient() {
        BookFilterDTO filter = new BookFilterDTO();
        filter.setPage(0);
        filter.setSize(5);

        Book book = new Book();
        BookDTO dto = new BookDTO();

        Page<Book> page = new PageImpl<>(List.of(book));

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        when(mapper.map(book, BookDTO.class)).thenReturn(dto);

        Page<BookDTO> result = service.getAllBooks(filter, false);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Get all books for employee with active filter")
    void getAllBooksEmployeeWithActiveFilter() {
        BookFilterDTO filter = new BookFilterDTO();
        filter.setPage(0);
        filter.setSize(5);
        filter.setActive(true);

        Book book = new Book();
        BookDTO dto = new BookDTO();

        Page<Book> page = new PageImpl<>(List.of(book));

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        when(mapper.map(book, BookDTO.class)).thenReturn(dto);

        Page<BookDTO> result = service.getAllBooks(filter, true);

        assertEquals(1, result.getTotalElements());
    }
}
