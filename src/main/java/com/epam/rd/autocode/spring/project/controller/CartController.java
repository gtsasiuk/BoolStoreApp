package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.order.BookDTO;
import com.epam.rd.autocode.spring.project.dto.order.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.order.CartDTO;
import com.epam.rd.autocode.spring.project.dto.order.OrderDTO;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RequestMapping("/cart")
@SessionAttributes("cart")
@RequiredArgsConstructor
public class CartController {
    private final BookService bookService;
    private final OrderService orderService;

    @ModelAttribute("cart")
    public CartDTO cart() {
        return new CartDTO();
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam String bookName,
                            @ModelAttribute("cart") CartDTO cart) {
        log.info("Add book to cart book={}", bookName);
        BookDTO book = bookService.getBookByName(bookName);
        if (book == null || Boolean.FALSE.equals(book.getActive())) {
            log.warn("Attempt to add inactive or missing book {}", bookName);
            throw new IllegalArgumentException("Book is not available");
        }
        cart.addBook(bookName);
        return "redirect:/cart";
    }

    @GetMapping
    public String cartPage(@ModelAttribute("cart") CartDTO cart, Model model) {
        log.info("Cart page opened itemsCount={}", cart.getItems().size());
        model.addAttribute("cart", cart);
        return "cart/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam String bookName,
                                 @RequestParam Integer quantity,
                                 @ModelAttribute("cart") CartDTO cart) {
        log.info("Update cart quantity book={} qty={}", bookName, quantity);

        if (bookName == null || bookName.isBlank()) {
            throw new IllegalArgumentException("Book name is required");
        }

        if (quantity <= 0) {
            cart.removeBook(bookName);
        } else {
            cart.updateQuantity(bookName, quantity);
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam String bookName,
                         @ModelAttribute("cart") CartDTO cart) {
        log.warn("Remove book from cart book={}", bookName);
        cart.removeBook(bookName);
        return "redirect:/cart";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/checkout")
    public String checkout(@ModelAttribute("cart") CartDTO cart,
                           Authentication auth,
                           SessionStatus status) {
        log.info("Checkout started client={}", auth.getName());

        if (cart.isEmpty()) {
            log.warn("Checkout attempted with empty cart");
            return "redirect:/cart";
        }

        for (BookItemDTO item : cart.getItems()) {
            BookDTO book = bookService.getBookByName(item.getBookName());

            if (book == null || Boolean.FALSE.equals(book.getActive())) {
                log.warn("Checkout failed inactive book {}", item.getBookName());
                throw new IllegalArgumentException("Book " + item.getBookName() + " is not available");
            }

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Invalid quantity for " + item.getBookName());
            }
        }

        OrderDTO order = new OrderDTO();
        order.setClientEmail(auth.getName());
        order.setEmployeeEmail(null);
        order.setOrderDate(LocalDateTime.now());
        order.setPrice(null);
        order.setBookItems(cart.getItems());
        order.setStatus(OrderStatus.NEW);

        orderService.addOrder(order);

        status.setComplete();
        log.info("Checkout completed client={}", auth.getName());
        return "redirect:/orders/success";
    }
}

