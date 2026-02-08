package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.filter.UserFilterDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.specification.ClientSpecs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {
    private ClientRepository repository;
    private ModelMapper mapper;

    @Override
    public ClientDTO addClient(ClientDTO client) {
        log.info("Creating client email={}", client.getEmail());
        if (repository.existsByEmail(client.getEmail())) {
            log.warn("Client already exists email={}", client.getEmail());
            throw new AlreadyExistException("Client already exists");
        }
        Client newClient = mapper.map(client, Client.class);
        Client savedClient = repository.save(newClient);
        log.info("Client created successfully email={}", client.getEmail());
        return mapper.map(savedClient, ClientDTO.class);
    }

    @Override
    public void decreaseBalance(String email, BigDecimal amount) {
        log.info("Decreasing balance email={} amount={}", email, amount);

        Client client = repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Client not found email={}", email);
                    return new NotFoundException("Client not found");
                });

        if (client.getBalance().compareTo(amount) < 0) {
            log.warn("Not enough balance email={} balance={} amount={}",
                    email, client.getBalance(), amount);
            throw new RuntimeException("Not enough balance");
        }

        client.setBalance(client.getBalance().subtract(amount));
        log.info("Balance decreased email={} newBalance={}",
                email, client.getBalance());
    }

    @Override
    public void toggleBlockByEmail(String email) {
        log.warn("Toggling block status for client email={}", email);

        Client client = repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Client not found email={}", email);
                    return new NotFoundException("Client not found");
                });

        client.setBlocked(!client.getBlocked());
        log.info("Client block status changed email={} blocked={}",
                email, client.getBlocked());
        repository.save(client);
    }

    @Override
    public Page<ClientDTO> getAllClients(UserFilterDTO filter) {
        log.info("Fetching clients page={} size={} search={} blocked={}",
                filter.getSafePage(),
                filter.getSafeSize(),
                filter.getSearch(),
                filter.getBlocked());

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
        log.info("Fetching client by email={}", email);

        Client client = repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Client not found email={}", email);
                    return new NotFoundException("Client not found");
                });
        return mapper.map(client, ClientDTO.class);
    }

    @Override
    public ClientDTO updateClientByEmail(String email, ClientDTO client) {
        log.info("Updating client email={}", email);
        Client existingClient = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        if (client.getPassword() != null && !client.getPassword().isBlank()) {
            existingClient.setPassword(client.getPassword());
        }

        existingClient.setName(client.getName());
        existingClient.setBalance(client.getBalance());
        Client updatedClient = repository.save(existingClient);
        log.info("Client updated successfully email={}", email);

        return mapper.map(updatedClient, ClientDTO.class);
    }

    @Override
    public void deleteClientByEmail(String email) {
        log.warn("Soft deleting client email={}", email);
        Client client = repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Client not found email={}", email);
                    return new NotFoundException("Client not found");
                });

        client.setBlocked(true);
        repository.save(client);
    }
}
