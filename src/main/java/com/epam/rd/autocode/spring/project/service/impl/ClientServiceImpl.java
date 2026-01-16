package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {
    private ClientRepository repository;
    private ModelMapper mapper;

    @Override
    public ClientDTO addClient(ClientDTO client) {
        Client newClient = mapper.map(client, Client.class);
        Client savedClient = repository.save(newClient);
        return mapper.map(savedClient, ClientDTO.class);
    }

    @Override
    public List<ClientDTO> getAllClients() {
        return repository.findAll()
                .stream().map(client -> mapper.map(client, ClientDTO.class)).toList();
    }

    @Override
    public ClientDTO getClientByEmail(String email) {
        Client client = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return mapper.map(client, ClientDTO.class);
    }

    @Override
    public ClientDTO updateClientByEmail(String email, ClientDTO client) {
        Client existingClient = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        existingClient.setEmail(client.getEmail());
        existingClient.setPassword(client.getPassword());
        existingClient.setName(client.getName());
        existingClient.setBalance(client.getBalance());
        Client updatedClient = repository.save(existingClient);

        return mapper.map(updatedClient, ClientDTO.class);
    }

    @Override
    public void deleteClientByEmail(String email) {
        Client client = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        repository.delete(client);
    }
}
