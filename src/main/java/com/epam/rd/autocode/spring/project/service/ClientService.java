package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.user.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.filter.UserFilterDTO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface ClientService {

    Page<ClientDTO> getAllClients(UserFilterDTO filter);

    ClientDTO getClientByEmail(String email);

    ClientDTO updateClientByEmail(String email, ClientDTO client);

    void deleteClientByEmail(String email);

    ClientDTO addClient(ClientDTO client);

    void decreaseBalance(String email, BigDecimal amount);

    void toggleBlockByEmail(String email);
}
