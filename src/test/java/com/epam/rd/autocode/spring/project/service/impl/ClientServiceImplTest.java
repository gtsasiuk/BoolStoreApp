package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.filter.UserFilterDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {
    @Mock
    private ClientRepository repository;
    @Mock
    private ModelMapper mapper;
    @InjectMocks
    private ClientServiceImpl service;

    @Test
    @DisplayName("Add client with unique email succeeds")
    void addClientSuccess() {
        ClientDTO dto = new ClientDTO();
        dto.setEmail("test@mail.com");

        Client entity = new Client();

        when(repository.existsByEmail("test@mail.com")).thenReturn(false);
        when(mapper.map(dto, Client.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.map(entity, ClientDTO.class)).thenReturn(dto);

        ClientDTO result = service.addClient(dto);

        assertEquals("test@mail.com", result.getEmail());
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Add client with existing email throws AlreadyExistException")
    void addClientAlreadyExists() {
        ClientDTO dto = new ClientDTO();
        dto.setEmail("test@mail.com");

        when(repository.existsByEmail("test@mail.com")).thenReturn(true);

        assertThrows(AlreadyExistException.class,
                () -> service.addClient(dto));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Decrease balance when enough money succeeds")
    void decreaseBalanceSuccess() {
        Client client = new Client();
        client.setBalance(BigDecimal.valueOf(100));

        when(repository.findByEmail("mail@test.com"))
                .thenReturn(Optional.of(client));

        service.decreaseBalance("mail@test.com", BigDecimal.valueOf(40));

        assertEquals(BigDecimal.valueOf(60), client.getBalance());
    }

    @Test
    @DisplayName("Decrease balance with insufficient funds throws RuntimeException")
    void decreaseBalanceNotEnoughMoney() {
        Client client = new Client();
        client.setBalance(BigDecimal.valueOf(20));

        when(repository.findByEmail("mail@test.com"))
                .thenReturn(Optional.of(client));

        assertThrows(RuntimeException.class,
                () -> service.decreaseBalance("mail@test.com", BigDecimal.valueOf(50)));
    }

    @Test
    @DisplayName("Decrease balance throws NotFoundException when client missing")
    void decreaseBalanceClientNotFound() {
        when(repository.findByEmail("mail@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.decreaseBalance("mail@test.com", BigDecimal.TEN));
    }

    @Test
    @DisplayName("Toggle block status switches blocked flag")
    void toggleBlockSuccess() {
        Client client = new Client();
        client.setBlocked(false);

        when(repository.findByEmail("mail@test.com"))
                .thenReturn(Optional.of(client));

        service.toggleBlockByEmail("mail@test.com");

        assertTrue(client.getBlocked());
        verify(repository).save(client);
    }

    @Test
    @DisplayName("Toggle block throws NotFoundException")
    void toggleBlockNotFound() {
        when(repository.findByEmail("mail@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.toggleBlockByEmail("mail@test.com"));
    }

    @Test
    @DisplayName("Get client by email returns DTO")
    void getClientByEmailSuccess() {
        Client client = new Client();
        ClientDTO dto = new ClientDTO();

        when(repository.findByEmail("mail@test.com"))
                .thenReturn(Optional.of(client));
        when(mapper.map(client, ClientDTO.class)).thenReturn(dto);

        ClientDTO result = service.getClientByEmail("mail@test.com");

        assertNotNull(result);
    }

    @Test
    @DisplayName("Get client by email throws NotFoundException")
    void getClientByEmailNotFound() {
        when(repository.findByEmail("mail@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.getClientByEmail("mail@test.com"));
    }

    @Test
    @DisplayName("Update client updates password when provided")
    void updateClientWithPassword() {
        Client existing = new Client();
        ClientDTO dto = new ClientDTO();
        dto.setPassword("new-pass");
        dto.setName("New Name");
        dto.setBalance(BigDecimal.TEN);

        when(repository.findByEmail("mail@test.com"))
                .thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.map(existing, ClientDTO.class)).thenReturn(dto);

        ClientDTO result = service.updateClientByEmail("mail@test.com", dto);

        assertEquals("New Name", result.getName());
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("Update client ignores blank password")
    void updateClientWithoutPassword() {
        Client existing = new Client();
        ClientDTO dto = new ClientDTO();
        dto.setPassword("   ");
        dto.setName("Name");

        when(repository.findByEmail("mail@test.com"))
                .thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.map(existing, ClientDTO.class)).thenReturn(dto);

        service.updateClientByEmail("mail@test.com", dto);

        verify(repository).save(existing);
    }

    @Test
    @DisplayName("Update client throws NotFoundException")
    void updateClientNotFound() {
        when(repository.findByEmail("mail@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.updateClientByEmail("mail@test.com", new ClientDTO()));
    }

    @Test
    @DisplayName("Delete client performs soft delete")
    void deleteClientSoftDelete() {
        Client client = new Client();
        client.setBlocked(false);

        when(repository.findByEmail("mail@test.com"))
                .thenReturn(Optional.of(client));

        service.deleteClientByEmail("mail@test.com");

        assertTrue(client.getBlocked());
        verify(repository).save(client);
    }

    @Test
    @DisplayName("Delete client throws NotFoundException")
    void deleteClientNotFound() {
        when(repository.findByEmail("mail@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.deleteClientByEmail("mail@test.com"));
    }

    @Test
    @DisplayName("Get all clients without filters")
    void getAllClientsNoFilters() {
        UserFilterDTO filter = new UserFilterDTO();
        filter.setPage(0);
        filter.setSize(5);

        Client client = new Client();
        ClientDTO dto = new ClientDTO();

        Page<Client> page = new PageImpl<>(List.of(client));

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        when(mapper.map(client, ClientDTO.class)).thenReturn(dto);

        Page<ClientDTO> result = service.getAllClients(filter);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Get all clients with search and blocked filter")
    void getAllClientsWithSearchAndBlocked() {
        UserFilterDTO filter = new UserFilterDTO();
        filter.setPage(0);
        filter.setSize(5);
        filter.setSearch("mail");
        filter.setBlocked(true);

        Client client = new Client();
        ClientDTO dto = new ClientDTO();

        Page<Client> page = new PageImpl<>(List.of(client));

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        when(mapper.map(client, ClientDTO.class)).thenReturn(dto);

        Page<ClientDTO> result = service.getAllClients(filter);

        assertEquals(1, result.getTotalElements());
    }
}
