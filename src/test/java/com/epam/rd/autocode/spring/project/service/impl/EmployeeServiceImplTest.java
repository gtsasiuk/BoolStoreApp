package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.filter.UserFilterDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {
    @Mock
    private EmployeeRepository repository;
    @Mock
    private ModelMapper mapper;
    @InjectMocks
    private EmployeeServiceImpl service;


    @Test
    @DisplayName("Add employee with unique email succeeds")
    void addEmployeeSuccess() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmail("emp@mail.com");

        Employee entity = new Employee();

        when(repository.existsByEmail("emp@mail.com")).thenReturn(false);
        when(mapper.map(dto, Employee.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.map(entity, EmployeeDTO.class)).thenReturn(dto);

        EmployeeDTO result = service.addEmployee(dto);

        assertEquals("emp@mail.com", result.getEmail());
        assertFalse(entity.getBlocked());
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Add employee with existing email throws AlreadyExistException")
    void addEmployeeAlreadyExists() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmail("emp@mail.com");

        when(repository.existsByEmail("emp@mail.com")).thenReturn(true);

        assertThrows(AlreadyExistException.class,
                () -> service.addEmployee(dto));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Toggle block switches blocked flag")
    void toggleBlockSuccess() {
        Employee employee = new Employee();
        employee.setBlocked(false);

        when(repository.findByEmail("emp@mail.com"))
                .thenReturn(Optional.of(employee));

        service.toggleBlockByEmail("emp@mail.com");

        assertTrue(employee.getBlocked());
        verify(repository).save(employee);
    }

    @Test
    @DisplayName("Toggle block throws NotFoundException")
    void toggleBlockNotFound() {
        when(repository.findByEmail("emp@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.toggleBlockByEmail("emp@mail.com"));
    }

    @Test
    @DisplayName("Get all employees without filters")
    void getAllEmployeesNoFilters() {
        UserFilterDTO filter = new UserFilterDTO();
        filter.setPage(0);
        filter.setSize(5);

        Employee employee = new Employee();
        EmployeeDTO dto = new EmployeeDTO();

        Page<Employee> page = new PageImpl<>(List.of(employee));

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        when(mapper.map(employee, EmployeeDTO.class)).thenReturn(dto);

        Page<EmployeeDTO> result = service.getAllEmployees(filter);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Get all employees with search and blocked filter")
    void getAllEmployeesWithFilters() {
        UserFilterDTO filter = new UserFilterDTO();
        filter.setPage(0);
        filter.setSize(5);
        filter.setSearch("emp");
        filter.setBlocked(true);

        Employee employee = new Employee();
        EmployeeDTO dto = new EmployeeDTO();

        Page<Employee> page = new PageImpl<>(List.of(employee));

        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        when(mapper.map(employee, EmployeeDTO.class)).thenReturn(dto);

        Page<EmployeeDTO> result = service.getAllEmployees(filter);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Get employee by email returns DTO")
    void getEmployeeByEmailSuccess() {
        Employee employee = new Employee();
        EmployeeDTO dto = new EmployeeDTO();

        when(repository.findByEmail("emp@mail.com"))
                .thenReturn(Optional.of(employee));
        when(mapper.map(employee, EmployeeDTO.class)).thenReturn(dto);

        EmployeeDTO result = service.getEmployeeByEmail("emp@mail.com");

        assertNotNull(result);
    }

    @Test
    @DisplayName("Get employee by email throws NotFoundException")
    void getEmployeeByEmailNotFound() {
        when(repository.findByEmail("emp@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.getEmployeeByEmail("emp@mail.com"));
    }
    @Test
    @DisplayName("Update employee updates password when provided")
    void updateEmployeeWithPassword() {
        Employee existing = new Employee();
        EmployeeDTO dto = new EmployeeDTO();
        dto.setPassword("new-pass");
        dto.setName("New Name");

        when(repository.findByEmail("emp@mail.com"))
                .thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.map(existing, EmployeeDTO.class)).thenReturn(dto);

        EmployeeDTO result = service.updateEmployeeByEmail("emp@mail.com", dto);

        assertEquals("New Name", result.getName());
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("Update employee ignores blank password")
    void updateEmployeeWithoutPassword() {
        Employee existing = new Employee();
        EmployeeDTO dto = new EmployeeDTO();
        dto.setPassword("   ");
        dto.setName("Name");

        when(repository.findByEmail("emp@mail.com"))
                .thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.map(existing, EmployeeDTO.class)).thenReturn(dto);

        service.updateEmployeeByEmail("emp@mail.com", dto);

        verify(repository).save(existing);
    }

    @Test
    @DisplayName("Update employee throws NotFoundException")
    void updateEmployeeNotFound() {
        when(repository.findByEmail("emp@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.updateEmployeeByEmail("emp@mail.com", new EmployeeDTO()));
    }

    @Test
    @DisplayName("Delete employee performs soft delete")
    void deleteEmployeeSoftDelete() {
        Employee employee = new Employee();
        employee.setBlocked(false);

        when(repository.findByEmail("emp@mail.com"))
                .thenReturn(Optional.of(employee));

        service.deleteEmployeeByEmail("emp@mail.com");

        assertTrue(employee.getBlocked());
        verify(repository).save(employee);
    }

    @Test
    @DisplayName("Delete employee throws NotFoundException")
    void deleteEmployeeNotFound() {
        when(repository.findByEmail("emp@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.deleteEmployeeByEmail("emp@mail.com"));
    }
}
