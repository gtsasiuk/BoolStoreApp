package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.filter.UserFilterDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EmployeeService employeeService;

    @Test
    @DisplayName("GET /employees returns employee list")
    @WithMockUser(roles = "EMPLOYEE")
    void getAllEmployees() throws Exception {
        EmployeeDTO employee = new EmployeeDTO("emp@mail.com", "Pass123", "Emp", "123-456-7890", LocalDate.now(), false);
        Page<EmployeeDTO> page = new PageImpl<>(List.of(employee));
        Mockito.when(employeeService.getAllEmployees(any(UserFilterDTO.class))).thenReturn(page);

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/employee_list"))
                .andExpect(model().attributeExists("employees"));
    }

    @Test
    @DisplayName("POST /employees/toggle-block")
    @WithMockUser(username = "admin@mail.com", roles = "EMPLOYEE")
    void toggleEmployeeBlock() throws Exception {
        mockMvc.perform(post("/employees/toggle-block")
                        .param("email", "emp@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/employees**"));

        Mockito.verify(employeeService).toggleBlockByEmail("emp@mail.com");
    }

    @Test
    @DisplayName("POST /employees/toggle-block cannot block self")
    @WithMockUser(username = "self@mail.com", roles = "EMPLOYEE")
    void toggleEmployeeBlockSelf() throws Exception {
        mockMvc.perform(post("/employees/toggle-block")
                        .param("email", "self@mail.com"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("GET /employees/new shows create form")
    @WithMockUser(roles = "EMPLOYEE")
    void newEmployeeForm() throws Exception {
        mockMvc.perform(get("/employees/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/employee_create"))
                .andExpect(model().attributeExists("employee"));
    }

    @Test
    @DisplayName("POST /employees/new with validation errors")
    @WithMockUser(roles = "EMPLOYEE")
    void createEmployeeValidationError() throws Exception {
        mockMvc.perform(post("/employees/new")
                        .param("email", "")
                        .param("password", "")
                        .param("name", "")
                        .param("phone", "")
                        .param("birthDate", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/employee_create"));
    }

    @Test
    @DisplayName("POST /employees/new already exists")
    @WithMockUser(roles = "EMPLOYEE")
    void createEmployeeAlreadyExists() throws Exception {
        doThrow(new AlreadyExistException("exists")).when(employeeService).addEmployee(any());

        mockMvc.perform(post("/employees/new")
                        .param("email", "emp@mail.com")
                        .param("password", "Pass123")
                        .param("name", "Emp")
                        .param("phone", "123-456-7890")
                        .param("birthDate", "2000-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("employees/employee_create"))
                .andExpect(model().attributeHasFieldErrors("employee", "email"));
    }

    @Test
    @DisplayName("POST /employees/new success")
    @WithMockUser(roles = "EMPLOYEE")
    void createEmployeeSuccess() throws Exception {
        mockMvc.perform(post("/employees/new")
                        .param("email", "emp@mail.com")
                        .param("password", "Pass123")
                        .param("name", "Emp")
                        .param("phone", "123-456-7890")
                        .param("birthDate", "2000-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"));
    }
}
