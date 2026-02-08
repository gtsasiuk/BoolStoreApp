package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.dto.filter.OrderFilterDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import com.epam.rd.autocode.spring.project.specification.OrderSpecs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
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
        log.info(
                "Creating order client={} itemsCount={}",
                order.getClientEmail(),
                order.getBookItems() != null ? order.getBookItems().size() : 0
        );

        Client client = clientRepository.findByEmail(order.getClientEmail())
                .orElseThrow(() -> {
                    log.warn("Client not found email={}", order.getClientEmail());
                    return new NotFoundException("Client not found");
                });
        Employee employee = null;
        if (order.getEmployeeEmail() != null) {
            employee = employeeRepository.findByEmail(order.getEmployeeEmail())
                    .orElseThrow(() -> {
                        log.warn("Employee not found email={}", order.getEmployeeEmail());
                        return  new NotFoundException("Employee not found");
                    });
        }

        Order newOrder = new Order();
        newOrder.setClient(client);
        newOrder.setEmployee(employee);
        newOrder.setOrderDate(order.getOrderDate());

        BigDecimal total = order.getBookItems().stream()
                .map(itemDTO -> {
                    Book book = bookRepository.findByName(itemDTO.getBookName())
                            .orElseThrow(() -> {
                                log.warn("Book not found name={}", itemDTO.getBookName());
                                return new NotFoundException("Book not found: " + itemDTO.getBookName());
                            });

                    return book.getPrice()
                            .multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        List<BookItem> items = order.getBookItems().stream()
                .map(itemDTO -> {
                    Book book = bookRepository.findByName(itemDTO.getBookName())
                            .orElseThrow(() ->
                            {
                               log.warn("Book not found name={}", itemDTO.getBookName());
                               return new NotFoundException("Book not found: " + itemDTO.getBookName());
                            });

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
        log.info("Order created totalPrice={}", total);
        return toDto(savedOrder);
    }

    @Override
    @Transactional
    public void confirmOrder(Long orderId, String employeeEmail) {
        log.warn("Order confirmation orderId={} employee={}", orderId, employeeEmail);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found for confirmation id={}", orderId);
                    return new NotFoundException("Order not found");
                });

        Employee employee = employeeRepository
                .findByEmail(employeeEmail)
                .orElseThrow(() -> {
                    log.warn("Employee not found email={}", employeeEmail);
                    return new NotFoundException("Employee not found");
                });

        order.setEmployee(employee);
        order.setOrderStatus(OrderStatus.CONFIRMED);

        clientService.decreaseBalance(order.getClient().getEmail(), order.getPrice());
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, String clientEmail) {
        log.warn("Order cancel request orderId={} client={}", orderId, clientEmail);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found id={}", orderId);
                    return new NotFoundException("Order not found");
                });

        if (!order.getClient().getEmail().equals(clientEmail)) {
            log.warn(
                    "Illegal order cancel attempt orderId={} expectedClient={} actualClient={}",
                    orderId,
                    order.getClient().getEmail(),
                    clientEmail
            );
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
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(OrderFilterDTO filter) {
        log.debug(
                "Fetching orders page={} size={} sort={} dir={} status={} search={}",
                filter.getSafePage(),
                filter.getSafeSize(),
                filter.getSafeSort(),
                filter.getSafeDir(),
                filter.getStatus(),
                filter.getSearch()
        );

        Pageable pageable = PageRequest.of(
                filter.getSafePage(),
                filter.getSafeSize(),
                Sort.by(
                        Sort.Direction.fromString(filter.getSafeDir()),
                        filter.getSafeSort()
                )
        );

        Specification<Order> spec = Specification.where(null);

        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            String search = filter.getSearch().toLowerCase();

            spec = spec.and(
                    Specification.where(OrderSpecs.clientEmailLike(search))
                            .or(OrderSpecs.employeeEmailLike(search))
            );
        }

        if (filter.getStatus() != null) {
            spec = spec.and(OrderSpecs.hasStatus(filter.getStatus()));
        }

        return orderRepository.findAll(spec, pageable)
                .map(this::toDto);
    }


    @Override
    @Transactional
    public OrderDTO getOrderById(Long id) {
        log.info("Fetching order id={}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order not found id={}", id);
                    return new RuntimeException("Order not found");
                });
        return toDto(order);
    }
}
