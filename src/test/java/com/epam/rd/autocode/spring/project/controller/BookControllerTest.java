package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.filter.BookFilterDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;

    private BookDTO createValidBook() {
        return new BookDTO(
                "Book1",
                "Author",
                "Genre",
                AgeGroup.ADULT,
                BigDecimal.TEN,
                LocalDate.of(2023, 1, 1),
                100,
                Language.ENGLISH,
                "Some characteristics",
                "Some description",
                true
        );
    }

    @Test
    @DisplayName("GET /books returns book list page")
    void getBooksPage() throws Exception {
        Page<BookDTO> books = new PageImpl<>(List.of(createValidBook()), PageRequest.of(0, 10), 1);
        Mockito.when(bookService.getAllBooks(any(BookFilterDTO.class), any(Boolean.class))).thenReturn(books);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/book_list"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    @DisplayName("GET /books/{name} returns book details if active")
    void getBookDetailsActive() throws Exception {
        BookDTO book = createValidBook();
        Mockito.when(bookService.getBookByName("Book1")).thenReturn(book);

        mockMvc.perform(get("/books/Book1"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/book_details"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    @DisplayName("GET /books/{name} throws access denied for inactive book")
    @WithMockUser(roles = "CUSTOMER")
    void getBookDetailsInactive() throws Exception {
        BookDTO book = createValidBook();
        book.setActive(false);
        Mockito.when(bookService.getBookByName("Book1")).thenReturn(book);

        mockMvc.perform(get("/books/Book1"))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/403"));;
    }

    @Test
    @DisplayName("GET /books/add form for employee")
    @WithMockUser(roles = "EMPLOYEE")
    void getAddForm() throws Exception {
        mockMvc.perform(get("/books/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/book_add"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("ageGroups"))
                .andExpect(model().attributeExists("languages"));
    }

    @Test
    @DisplayName("POST /books/add validation error")
    @WithMockUser(roles = "EMPLOYEE")
    void addBookValidationError() throws Exception {
        mockMvc.perform(post("/books/add")
                        .param("name", "")
                        .param("author", "")
                        .param("genre", "")
                        .param("ageGroup", "")
                        .param("price", "")
                        .param("publicationDate", "")
                        .param("pages", "")
                        .param("language", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("books/book_add"));
    }

    @Test
    @DisplayName("POST /books/add already exists")
    @WithMockUser(roles = "EMPLOYEE")
    void addBookAlreadyExists() throws Exception {
        doThrow(new AlreadyExistException("exists"))
                .when(bookService).addBook(any(BookDTO.class));

        mockMvc.perform(post("/books/add")
                        .param("name", "Book1")
                        .param("author", "Author")
                        .param("genre", "Genre")
                        .param("ageGroup", "ADULT")
                        .param("price", "10")
                        .param("publicationDate", "2023-01-01")
                        .param("pages", "100")
                        .param("language", "ENGLISH"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/book_add"))
                .andExpect(model().attributeHasFieldErrors("book", "name"));
    }

    @Test
    @DisplayName("POST /books/add success")
    @WithMockUser(roles = "EMPLOYEE")
    void addBookSuccess() throws Exception {
        mockMvc.perform(post("/books/add")
                        .param("name", "Book1")
                        .param("author", "Author")
                        .param("genre", "Genre")
                        .param("ageGroup", "ADULT")
                        .param("price", "10")
                        .param("publicationDate", "2023-01-01")
                        .param("pages", "100")
                        .param("language", "ENGLISH"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));
    }

    @Test
    @DisplayName("GET /books/edit/{name} form")
    @WithMockUser(roles = "EMPLOYEE")
    void editBookForm() throws Exception {
        Mockito.when(bookService.getBookByName("Book1")).thenReturn(createValidBook());

        mockMvc.perform(get("/books/edit/Book1"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/book_edit"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("ageGroups"))
                .andExpect(model().attributeExists("languages"));
    }

    @Test
    @DisplayName("POST /books/edit/{name} validation error")
    @WithMockUser(roles = "EMPLOYEE")
    void editBookValidationError() throws Exception {
        mockMvc.perform(post("/books/edit/Book1")
                        .param("name", "")
                        .param("author", "")
                        .param("genre", "")
                        .param("ageGroup", "")
                        .param("price", "")
                        .param("publicationDate", "")
                        .param("pages", "")
                        .param("language", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("books/book_edit"));
    }

    @Test
    @DisplayName("POST /books/edit/{name} success")
    @WithMockUser(roles = "EMPLOYEE")
    void editBookSuccess() throws Exception {
        mockMvc.perform(post("/books/edit/Book1")
                        .param("name", "Book1 Updated")
                        .param("author", "Author Updated")
                        .param("genre", "Genre")
                        .param("ageGroup", "ADULT")
                        .param("price", "15")
                        .param("publicationDate", "2023-01-01")
                        .param("pages", "120")
                        .param("language", "ENGLISH"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/Book1"));
    }

    @Test
    @DisplayName("POST /books/toggle-active/{name} success")
    @WithMockUser(roles = "EMPLOYEE")
    void toggleBookActive() throws Exception {
        mockMvc.perform(post("/books/toggle-active/Book1"))
                .andExpect(status().is3xxRedirection());
    }
}
