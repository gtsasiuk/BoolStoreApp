package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private OrderRepository orderRepository;
    private ClientRepository clientRepository;
    private EmployeeRepository employeeRepository;
    private BookRepository bookRepository;
    private ClientService clientService;

    private OrderDTO toDto(Order order) {
        OrderDTO dto = new OrderDTO();

        dto.setId(order.getId());
        dto.setClientEmail(order.getClient().getEmail());
        if (order.getEmployee() != null) {
            dto.setEmployeeEmail(order.getEmployee().getEmail());
        } else {
            dto.setEmployeeEmail(null);
        }
        dto.setOrderDate(order.getOrderDate());
        dto.setPrice(order.getPrice());
        dto.setStatus(order.getOrderStatus());

        List<BookItemDTO> items = order.getBookItems().stream()
                .map(item -> new BookItemDTO(
                        item.getBook().getName(),
                        item.getQuantity()
                ))
                .toList();

        dto.setBookItems(items);

        return dto;
    }

    @Override
    @Transactional
    public OrderDTO addOrder(OrderDTO order) {
        Client client = clientRepository.findByEmail(order.getClientEmail())
                .orElseThrow(() -> new RuntimeException("Client not found"));
        Employee employee = null;
        if (order.getEmployeeEmail() != null) {
            employee = employeeRepository.findByEmail(order.getEmployeeEmail())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
        }

        Order newOrder = new Order();
        newOrder.setClient(client);
        newOrder.setEmployee(employee);
        newOrder.setOrderDate(order.getOrderDate());

        BigDecimal total = order.getBookItems().stream()
                .map(itemDTO -> {
                    Book book = bookRepository.findByName(itemDTO.getBookName())
                            .orElseThrow(() -> new RuntimeException("Book not found"));

                    return book.getPrice()
                            .multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        List<BookItem> items = order.getBookItems().stream()
                .map(itemDTO -> {
                    Book book = bookRepository.findByName(itemDTO.getBookName())
                            .orElseThrow(() ->
                                    new RuntimeException("Book not found: " + itemDTO.getBookName()));

                    BookItem item = new BookItem();
                    item.setBook(book);
                    item.setQuantity(itemDTO.getQuantity());
                    item.setOrder(newOrder);

                    return item;
                })
                .toList();

        newOrder.setPrice(total);
        newOrder.setBookItems(items);
        newOrder.setOrderStatus(OrderStatus.NEW);

        Order savedOrder = orderRepository.save(newOrder);

        return toDto(savedOrder);
    }

    @Override
    @Transactional
    public void confirmOrder(Long orderId, String employeeEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow();

        Employee employee = employeeRepository
                .findByEmail(employeeEmail)
                .orElseThrow();

        order.setEmployee(employee);
        order.setOrderStatus(OrderStatus.CONFIRMED);

        clientService.decreaseBalance(order.getClient().getEmail(), order.getPrice());
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, String clientEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getClient().getEmail().equals(clientEmail)) {
            throw new RuntimeException("You cannot cancel someone else's order");
        }

        if (order.getOrderStatus() != OrderStatus.NEW) {
            throw new RuntimeException("Only NEW orders can be cancelled");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByClient(String clientEmail) {
        return orderRepository.findAllByClient_Email(clientEmail)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByEmployee(String employeeEmail) {
        return orderRepository.findAllByEmployee_Email(employeeEmail)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return toDto(order);
    }
}
