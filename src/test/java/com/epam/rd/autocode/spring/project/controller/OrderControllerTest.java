package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.dto.filter.OrderFilterDTO;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private OrderDTO sampleOrder() {
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setClientEmail("client@mail.com");
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);
        return order;
    }

    @Test
    @DisplayName("GET /orders/success returns success page")
    @WithMockUser(roles = "CUSTOMER")
    void orderSuccessPage() throws Exception {
        mockMvc.perform(get("/orders/success"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/success"));
    }

    @Test
    @DisplayName("GET /orders for employee")
    @WithMockUser(roles = "EMPLOYEE")
    void ordersPageEmployee() throws Exception {
        Mockito.when(orderService.getAllOrders(any(OrderFilterDTO.class)))
                .thenReturn(new PageImpl<>(List.of(sampleOrder())));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/orders_list"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    @DisplayName("GET /orders/my for customer")
    @WithMockUser(roles = "CUSTOMER", username = "client@mail.com")
    void myOrdersPage() throws Exception {
        Mockito.when(orderService.getOrdersByClient("client@mail.com"))
                .thenReturn(List.of(sampleOrder()));

        mockMvc.perform(get("/orders/my"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/my_orders"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    @DisplayName("GET /orders/{id} details for any user")
    @WithMockUser(roles = "CUSTOMER")
    void orderDetails() throws Exception {
        Mockito.when(orderService.getOrderById(1L))
                .thenReturn(sampleOrder());

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders/order_details"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    @DisplayName("POST /orders/confirm/{id} for employee")
    @WithMockUser(roles = "EMPLOYEE", username = "employee@mail.com")
    void confirmOrder() throws Exception {
        mockMvc.perform(post("/orders/confirm/1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("POST /orders/cancel/{id} for customer")
    @WithMockUser(roles = "CUSTOMER", username = "client@mail.com")
    void cancelOrder() throws Exception {
        mockMvc.perform(post("/orders/cancel/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/my"));
    }
}

