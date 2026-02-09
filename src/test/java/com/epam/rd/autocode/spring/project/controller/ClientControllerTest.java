package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.filter.UserFilterDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ClientService clientService;

    @Test
    @DisplayName("GET /clients returns client list")
    @WithMockUser(roles = "EMPLOYEE")
    void getAllClients() throws Exception {
        ClientDTO client = new ClientDTO("user@mail.com", "pass", "User", BigDecimal.valueOf(100), false);
        Page<ClientDTO> page = new PageImpl<>(List.of(client));
        Mockito.when(clientService.getAllClients(any(UserFilterDTO.class))).thenReturn(page);

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(view().name("clients/client_list"))
                .andExpect(model().attributeExists("clients"));
    }

    @Test
    @DisplayName("POST /clients/toggle-block")
    @WithMockUser(roles = "EMPLOYEE")
    void toggleClientBlock() throws Exception {
        mockMvc.perform(post("/clients/toggle-block")
                        .param("email", "user@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/clients**"));

        Mockito.verify(clientService).toggleBlockByEmail("user@mail.com");
    }
}
