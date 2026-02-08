package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.order.BookDTO;
import com.epam.rd.autocode.spring.project.dto.order.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.order.CartDTO;
import com.epam.rd.autocode.spring.project.dto.order.OrderDTO;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.time.LocalDateTime;

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
        BookDTO book = bookService.getBookByName(bookName);
        if (book == null || Boolean.FALSE.equals(book.getActive())) {
            throw new IllegalArgumentException("Book is not available");
        }
        cart.addBook(bookName);
        return "redirect:/cart";
    }

    @GetMapping
    public String cartPage(@ModelAttribute("cart") CartDTO cart, Model model) {
        model.addAttribute("cart", cart);
        return "cart/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam String bookName,
                                 @RequestParam Integer quantity,
                                 @ModelAttribute("cart") CartDTO cart) {

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
        cart.removeBook(bookName);
        return "redirect:/cart";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/checkout")
    public String checkout(@ModelAttribute("cart") CartDTO cart,
                           Authentication auth,
                           SessionStatus status) {

        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        for (BookItemDTO item : cart.getItems()) {
            BookDTO book = bookService.getBookByName(item.getBookName());

            if (book == null || Boolean.FALSE.equals(book.getActive())) {
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
        return "redirect:/orders/success";
    }
}

