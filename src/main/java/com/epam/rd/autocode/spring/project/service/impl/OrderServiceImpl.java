package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private OrderRepository orderRepository;
    private ClientRepository clientRepository;
    private EmployeeRepository employeeRepository;
    private BookRepository bookRepository;

    private OrderDTO toDto(Order order) {
        OrderDTO dto = new OrderDTO();

        dto.setClientEmail(order.getClient().getEmail());
        dto.setEmployeeEmail(order.getEmployee().getEmail());
        dto.setOrderDate(order.getOrderDate());
        dto.setPrice(order.getPrice());

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
        Employee employee = employeeRepository.findByEmail(order.getEmployeeEmail())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Order newOrder = new Order();
        newOrder.setClient(client);
        newOrder.setEmployee(employee);
        newOrder.setPrice(order.getPrice());
        newOrder.setOrderDate(order.getOrderDate());
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
        newOrder.setBookItems(items);

        Order savedOrder = orderRepository.save(newOrder);

        return toDto(savedOrder);
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

}
