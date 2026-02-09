package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.CartDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;
    @MockBean
    private OrderService orderService;

    private BookDTO activeBook() {
        BookDTO book = new BookDTO();
        book.setName("Book1");
        book.setActive(true);
        book.setPrice(BigDecimal.TEN);
        return book;
    }

    private BookDTO inactiveBook() {
        BookDTO book = new BookDTO();
        book.setName("Book2");
        book.setActive(false);
        return book;
    }

    @Test
    @DisplayName("GET /cart shows cart page")
    @WithMockUser(roles = "CUSTOMER")
    void cartPage() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/cart"))
                .andExpect(model().attributeExists("cart"));
    }

    @Test
    @DisplayName("POST /cart/add adds active book")
    @WithMockUser(roles = "CUSTOMER")
    void addToCartActiveBook() throws Exception {
        Mockito.when(bookService.getBookByName("Book1")).thenReturn(activeBook());

        mockMvc.perform(post("/cart/add").param("bookName", "Book1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    @DisplayName("POST /cart/add fails for inactive book")
    void addToCartInactiveBook() throws Exception {
        Mockito.when(bookService.getBookByName("Book2")).thenReturn(inactiveBook());

        mockMvc.perform(post("/cart/add").param("bookName", "Book2"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /cart/update updates quantity or removes")
    @WithMockUser(roles = "CUSTOMER")
    void updateQuantity() throws Exception {
        CartDTO cart = new CartDTO(new ArrayList<>(List.of(new BookItemDTO("Book1", 2))));

        mockMvc.perform(post("/cart/update")
                        .param("bookName", "Book1")
                        .param("quantity", "5")
                        .sessionAttr("cart", cart))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        mockMvc.perform(post("/cart/update")
                        .param("bookName", "Book1")
                        .param("quantity", "0")
                        .sessionAttr("cart", cart))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }


    @Test
    @DisplayName("POST /cart/remove removes book")
    @WithMockUser(roles = "CUSTOMER")
    void removeBook() throws Exception {
        CartDTO cart = new CartDTO(new ArrayList<>(List.of(new BookItemDTO("Book1", 2))));

        mockMvc.perform(post("/cart/remove")
                        .param("bookName", "Book1")
                        .sessionAttr("cart", cart))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    @DisplayName("POST /cart/checkout success for authenticated customer")
    @WithMockUser(roles = "CUSTOMER", username = "test@mail.com")
    void checkoutSuccess() throws Exception {
        CartDTO cart = new CartDTO(List.of(new BookItemDTO("Book1", 1)));
        Mockito.when(bookService.getBookByName("Book1")).thenReturn(activeBook());

        mockMvc.perform(post("/cart/checkout")
                        .sessionAttr("cart", cart))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/success"));
    }

    @Test
    @DisplayName("POST /cart/checkout fails with empty cart")
    @WithMockUser(roles = "CUSTOMER", username = "test@mail.com")
    void checkoutEmptyCart() throws Exception {
        CartDTO cart = new CartDTO();

        mockMvc.perform(post("/cart/checkout")
                        .sessionAttr("cart", cart))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }
}
