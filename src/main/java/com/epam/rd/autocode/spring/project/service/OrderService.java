package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.order.OrderDTO;
import com.epam.rd.autocode.spring.project.dto.filter.OrderFilterDTO;
import org.springframework.data.domain.Page;

import java.util.*;

public interface OrderService {

    List<OrderDTO> getOrdersByClient(String clientEmail);

    List<OrderDTO> getOrdersByEmployee(String employeeEmail);

    OrderDTO addOrder(OrderDTO order);

    Page<OrderDTO> getAllOrders(OrderFilterDTO filter);

    OrderDTO getOrderById(Long id);

    void confirmOrder(Long orderId, String employeeEmail);

    void cancelOrder(Long orderId, String clientEmail);
}
