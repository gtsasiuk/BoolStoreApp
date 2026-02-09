package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.*;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ClientService clientService;
    @MockBean
    private EmployeeService employeeService;

    private ClientDTO sampleClient() {
        ClientDTO client = new ClientDTO();
        client.setName("Client Name");
        client.setEmail("client@mail.com");
        client.setBalance(BigDecimal.valueOf(100));
        client.setBlocked(false);
        return client;
    }

    private EmployeeDTO sampleEmployee() {
        EmployeeDTO emp = new EmployeeDTO();
        emp.setEmail("employee@mail.com");
        emp.setName("Employee Name");
        emp.setPhone("123-456-7890");
        emp.setBirthDate(LocalDate.of(1990, 1, 1));
        emp.setBlocked(false);
        emp.setPassword("secret213");
        return emp;
    }

    @Test
    @DisplayName("GET /profile for client returns profile page")
    @WithMockUser(username = "client@mail.com", roles = "CUSTOMER")
    void getProfileClient() throws Exception {
        Mockito.when(clientService.getClientByEmail("client@mail.com")).thenReturn(sampleClient());

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("clientForm"))
                .andExpect(model().attributeExists("edit"));
    }

    @Test
    @DisplayName("GET /profile for employee returns profile page")
    @WithMockUser(username = "employee@mail.com", roles = "EMPLOYEE")
    void getProfileEmployee() throws Exception {
        Mockito.when(employeeService.getEmployeeByEmail("employee@mail.com")).thenReturn(sampleEmployee());

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("employeeForm"))
                .andExpect(model().attributeExists("edit"));
    }

    @Test
    @DisplayName("POST /profile updates client profile successfully")
    @WithMockUser(username = "client@mail.com", roles = "CUSTOMER")
    void updateClientProfileSuccess() throws Exception {
        Mockito.when(clientService.getClientByEmail("client@mail.com")).thenReturn(sampleClient());

        mockMvc.perform(post("/profile/client")
                        .param("name", "Updated Client")
                        .param("balance", "200"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        Mockito.verify(clientService).updateClientByEmail(any(), any(ClientDTO.class));
    }

    @Test
    @DisplayName("POST /profile validation fails for client")
    @WithMockUser(username = "client@mail.com", roles = "CUSTOMER")
    void updateClientProfileValidationFail() throws Exception {
        Mockito.when(clientService.getClientByEmail("client@mail.com")).thenReturn(sampleClient());

        mockMvc.perform(post("/profile/client")
                        .param("clientForm.name", "") // name is blank → validation fail
                        .param("clientForm.balance", "200"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("clientForm"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("edit", true));
    }

    @Test
    @DisplayName("POST /profile updates employee profile successfully")
    @WithMockUser(username = "employee@mail.com", roles = "EMPLOYEE")
    void updateEmployeeProfileSuccess() throws Exception {
        Mockito.when(employeeService.getEmployeeByEmail("employee@mail.com")).thenReturn(sampleEmployee());

        mockMvc.perform(post("/profile/employee")
                        .param("name", "Updated Employee")
                        .param("phone", "123-456-7890")
                        .param("birthDate", "1990-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        Mockito.verify(employeeService).updateEmployeeByEmail(any(), any(EmployeeDTO.class));

    }

    @Test
    @DisplayName("POST /profile validation fails for employee")
    @WithMockUser(username = "employee@mail.com", roles = "EMPLOYEE")
    void updateEmployeeProfileValidationFail() throws Exception {
        Mockito.when(employeeService.getEmployeeByEmail("employee@mail.com")).thenReturn(sampleEmployee());

        mockMvc.perform(post("/profile/employee")
                        .param("employeeForm.name", "") // invalid blank name
                        .param("employeeForm.phone", "123-456-7890")
                        .param("employeeForm.birthDate", "1990-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("employeeForm"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("edit", true));
    }

    @Test
    @DisplayName("POST /profile/delete deletes client profile")
    @WithMockUser(username = "client@mail.com", roles = "CUSTOMER")
    void deleteClientProfile() throws Exception {
        mockMvc.perform(post("/profile/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/logout"));

        Mockito.verify(clientService).deleteClientByEmail("client@mail.com");
    }
}
