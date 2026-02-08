package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.order.OrderDTO;
import com.epam.rd.autocode.spring.project.dto.filter.OrderFilterDTO;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/success")
    public String orderSuccess() {
        return "orders/success";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping
    public String ordersPage(@ModelAttribute("filter") OrderFilterDTO filter, Model model) {
        Page<OrderDTO> orders = orderService.getAllOrders(filter);
        model.addAttribute("orders", orders);
        return "orders/orders_list";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("my")
    public String myOrdersPage(Model model, Authentication auth) {
        model.addAttribute("orders", orderService.getOrdersByClient(auth.getName()));
        return "orders/my_orders";
    }

    @PreAuthorize("hasAnyRole('CUSTOMER','EMPLOYEE')")
    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getOrderById(id));
        return "orders/order_details";
    }


    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/confirm/{id}")
    public String orderConfirm(@PathVariable Long id, @ModelAttribute("filter") OrderFilterDTO filter, Authentication auth) {
        orderService.confirmOrder(id, auth.getName());
        return "redirect:/orders?" + filter.toQueryString();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/cancel/{id}")
    public String orderCancel(@PathVariable Long id, Authentication auth) {
        orderService.cancelOrder(id, auth.getName());
        return "redirect:/orders/my";
    }
}
