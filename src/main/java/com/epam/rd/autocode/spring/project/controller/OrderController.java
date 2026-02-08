package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.dto.filter.OrderFilterDTO;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/success")
    public String orderSuccess() {
        log.info("Order success page opened");
        return "orders/success";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping
    public String ordersPage(@ModelAttribute("filter") OrderFilterDTO filter, Model model) {
        log.info("Orders list requested filter={}", filter);
        Page<OrderDTO> orders = orderService.getAllOrders(filter);
        model.addAttribute("orders", orders);
        return "orders/orders_list";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("my")
    public String myOrdersPage(Model model, Authentication auth) {
        log.info("Customer orders requested email={}", auth.getName());
        model.addAttribute("orders", orderService.getOrdersByClient(auth.getName()));
        return "orders/my_orders";
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','EMPLOYEE')")
    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        log.info("Order details requested id={}", id);
        model.addAttribute("order", orderService.getOrderById(id));
        return "orders/order_details";
    }


    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/confirm/{id}")
    public String orderConfirm(@PathVariable Long id, @ModelAttribute("filter") OrderFilterDTO filter, Authentication auth) {
        log.warn("Order confirmation id={} employee={}", id, auth.getName());
        orderService.confirmOrder(id, auth.getName());
        return "redirect:/orders?" + filter.toQueryString();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/cancel/{id}")
    public String orderCancel(@PathVariable Long id, Authentication auth) {
        log.warn("Order cancel request id={} client={}", id, auth.getName());
        orderService.cancelOrder(id, auth.getName());
        return "redirect:/orders/my";
    }
}
