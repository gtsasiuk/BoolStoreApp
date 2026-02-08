package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.user.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.filter.UserFilterDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.specification.ClientSpecs;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {
    private ClientRepository repository;
    private ModelMapper mapper;

    @Override
    public ClientDTO addClient(ClientDTO client) {
        if (repository.existsByEmail(client.getEmail())) {
            throw new AlreadyExistException("Client already exists");
        }
        Client newClient = mapper.map(client, Client.class);
        Client savedClient = repository.save(newClient);
        return mapper.map(savedClient, ClientDTO.class);
    }

    @Override
    public void decreaseBalance(String email, BigDecimal amount) {
        Client client = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        if (client.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Not enough balance");
        }

        client.setBalance(client.getBalance().subtract(amount));
    }

    @Override
    public void toggleBlockByEmail(String email) {
        Client client = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        client.setBlocked(!client.getBlocked());
        repository.save(client);
    }

    @Override
    public Page<ClientDTO> getAllClients(UserFilterDTO filter) {
        Pageable pageable = PageRequest.of(
                filter.getSafePage(),
                filter.getSafeSize(),
                Sort.by(Sort.Direction.fromString(filter.getSafeDir()), filter.getSafeSort())
        );

        Specification<Client> spec = Specification.where(null);

        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            spec = spec.and(ClientSpecs.nameOrEmailContains(filter.getSearch()));
        }

        if (filter.getBlocked() != null) {
            spec = spec.and(ClientSpecs.hasBlockedStatus(filter.getBlocked()));
        }

        return repository.findAll(spec, pageable)
                .map(client -> mapper.map(client, ClientDTO.class));
    }

    @Override
    public ClientDTO getClientByEmail(String email) {
        Client client = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        return mapper.map(client, ClientDTO.class);
    }

    @Override
    public ClientDTO updateClientByEmail(String email, ClientDTO client) {
        Client existingClient = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        if (client.getPassword() != null && !client.getPassword().isBlank()) {
            existingClient.setPassword(client.getPassword());
        }

        existingClient.setName(client.getName());
        existingClient.setBalance(client.getBalance());
        Client updatedClient = repository.save(existingClient);

        return mapper.map(updatedClient, ClientDTO.class);
    }

    @Override
    public void deleteClientByEmail(String email) {
        Client client = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        client.setBlocked(true);
        repository.save(client);
    }
}
