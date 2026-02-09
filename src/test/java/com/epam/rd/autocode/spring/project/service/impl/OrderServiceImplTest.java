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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ClientService clientService;
    @InjectMocks
    private OrderServiceImpl service;


    @Test
    @DisplayName("Add order without employee calculates total and saves")
    void addOrderSuccess() {
        Client client = new Client();
        client.setEmail("client@mail.com");

        Book book = new Book();
        book.setName("Book");
        book.setPrice(BigDecimal.TEN);

        OrderDTO dto = new OrderDTO();
        dto.setClientEmail("client@mail.com");
        dto.setOrderDate(LocalDateTime.now());
        dto.setBookItems(List.of(new BookItemDTO("Book", 2)));

        when(clientRepository.findByEmail("client@mail.com"))
                .thenReturn(Optional.of(client));
        when(bookRepository.findByName("Book"))
                .thenReturn(Optional.of(book));
        when(orderRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        OrderDTO result = service.addOrder(dto);

        assertEquals(BigDecimal.valueOf(20), result.getPrice());
        assertEquals(OrderStatus.NEW, result.getStatus());
        verify(orderRepository).save(any());
    }

    @Test
    @DisplayName("Add order with employee succeeds")
    void addOrderWithEmployee() {
        Client client = new Client();
        client.setEmail("client@mail.com");

        Employee employee = new Employee();
        employee.setEmail("emp@mail.com");

        Book book = new Book();
        book.setName("Book");
        book.setPrice(BigDecimal.TEN);

        OrderDTO dto = new OrderDTO();
        dto.setClientEmail("client@mail.com");
        dto.setEmployeeEmail("emp@mail.com");
        dto.setBookItems(List.of(new BookItemDTO("Book", 1)));

        when(clientRepository.findByEmail("client@mail.com"))
                .thenReturn(Optional.of(client));
        when(employeeRepository.findByEmail("emp@mail.com"))
                .thenReturn(Optional.of(employee));
        when(bookRepository.findByName("Book"))
                .thenReturn(Optional.of(book));
        when(orderRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        OrderDTO result = service.addOrder(dto);

        assertEquals("emp@mail.com", result.getEmployeeEmail());
    }

    @Test
    @DisplayName("Add order throws NotFoundException when client missing")
    void addOrderClientNotFound() {
        OrderDTO dto = new OrderDTO();
        dto.setClientEmail("client@mail.com");

        when(clientRepository.findByEmail("client@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.addOrder(dto));
    }

    @Test
    @DisplayName("Add order throws NotFoundException when book missing")
    void addOrderBookNotFound() {
        Client client = new Client();
        client.setEmail("client@mail.com");

        OrderDTO dto = new OrderDTO();
        dto.setClientEmail("client@mail.com");
        dto.setBookItems(List.of(new BookItemDTO("Unknown", 1)));

        when(clientRepository.findByEmail("client@mail.com"))
                .thenReturn(Optional.of(client));
        when(bookRepository.findByName("Unknown"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.addOrder(dto));
    }

    @Test
    @DisplayName("Confirm order assigns employee and decreases balance")
    void confirmOrderSuccess() {
        Client client = new Client();
        client.setEmail("client@mail.com");

        Employee employee = new Employee();
        employee.setEmail("emp@mail.com");

        Order order = new Order();
        order.setClient(client);
        order.setPrice(BigDecimal.TEN);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));
        when(employeeRepository.findByEmail("emp@mail.com"))
                .thenReturn(Optional.of(employee));

        service.confirmOrder(1L, "emp@mail.com");

        assertEquals(OrderStatus.CONFIRMED, order.getOrderStatus());
        assertEquals(employee, order.getEmployee());
        verify(clientService)
                .decreaseBalance("client@mail.com", BigDecimal.TEN);
    }

    @Test
    @DisplayName("Confirm order throws NotFoundException when employee missing")
    void confirmOrderEmployeeNotFound() {
        Order order = new Order();
        Client client = new Client();
        client.setEmail("client@mail.com");
        order.setClient(client);
        order.setPrice(BigDecimal.TEN);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));
        when(employeeRepository.findByEmail("emp@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.confirmOrder(1L, "emp@mail.com"));
    }


    @Test
    @DisplayName("Cancel order with correct client and NEW status")
    void cancelOrderSuccess() {
        Client client = new Client();
        client.setEmail("client@mail.com");

        Order order = new Order();
        order.setClient(client);
        order.setOrderStatus(OrderStatus.NEW);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        service.cancelOrder(1L, "client@mail.com");

        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
    }

    @Test
    @DisplayName("Cancel order with wrong client throws exception")
    void cancelOrderWrongClient() {
        Client client = new Client();
        client.setEmail("real@mail.com");

        Order order = new Order();
        order.setClient(client);
        order.setOrderStatus(OrderStatus.NEW);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class,
                () -> service.cancelOrder(1L, "fake@mail.com"));
    }

    @Test
    @DisplayName("Cancel order throws NotFoundException when order missing")
    void cancelOrderOrderNotFound() {
        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.cancelOrder(1L, "client@mail.com"));
    }


    @Test
    @DisplayName("Cancel order with non-NEW status throws exception")
    void cancelOrderNotNew() {
        Client client = new Client();
        client.setEmail("client@mail.com");

        Order order = new Order();
        order.setClient(client);
        order.setOrderStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class,
                () -> service.cancelOrder(1L, "client@mail.com"));
    }

    @Test
    @DisplayName("Get orders by client returns list")
    void getOrdersByClient() {
        Order order = new Order();
        Client client = new Client();
        client.setEmail("client@mail.com");
        order.setClient(client);

        when(orderRepository.findAllByClient_Email("client@mail.com"))
                .thenReturn(List.of(order));

        List<OrderDTO> result = service.getOrdersByClient("client@mail.com");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Get orders by employee returns list")
    void getOrdersByEmployee() {
        Order order = new Order();

        Client client = new Client();
        client.setEmail("client@mail.com");
        order.setClient(client);

        Employee employee = new Employee();
        employee.setEmail("emp@mail.com");
        order.setEmployee(employee);

        when(orderRepository.findAllByEmployee_Email("emp@mail.com"))
                .thenReturn(List.of(order));

        List<OrderDTO> result = service.getOrdersByEmployee("emp@mail.com");

        assertEquals(1, result.size());
        assertEquals("client@mail.com", result.get(0).getClientEmail());
        assertEquals("emp@mail.com", result.get(0).getEmployeeEmail());
    }


    @Test
    @DisplayName("Get all orders without filters")
    void getAllOrdersNoFilters() {
        OrderFilterDTO filter = new OrderFilterDTO();
        filter.setPage(0);
        filter.setSize(5);

        Order order = new Order();
        Client client = new Client();
        client.setEmail("client@mail.com");
        order.setClient(client);

        Page<Order> page = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        Page<OrderDTO> result = service.getAllOrders(filter);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Get all orders with search and status")
    void getAllOrdersWithFilters() {
        OrderFilterDTO filter = new OrderFilterDTO();
        filter.setPage(0);
        filter.setSize(5);
        filter.setSearch("mail");
        filter.setStatus(OrderStatus.NEW);

        Order order = new Order();
        Client client = new Client();
        client.setEmail("client@mail.com");
        order.setClient(client);

        Page<Order> page = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        Page<OrderDTO> result = service.getAllOrders(filter);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Get order by id returns DTO")
    void getOrderByIdSuccess() {
        Order order = new Order();
        Client client = new Client();
        client.setEmail("client@mail.com");
        order.setClient(client);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        OrderDTO result = service.getOrderById(1L);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Get order by id throws exception when missing")
    void getOrderByIdNotFound() {
        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.getOrderById(1L));
    }
}
