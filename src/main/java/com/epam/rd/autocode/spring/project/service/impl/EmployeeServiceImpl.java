package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository repository;
    private ModelMapper mapper;

    @Override
    public EmployeeDTO addEmployee(EmployeeDTO employee) {
        if (repository.existsByEmail(employee.getEmail())) {
            throw new AlreadyExistException("Employee already exists");
        }
        Employee newEmployee = mapper.map(employee, Employee.class);
        Employee savedEmployee = repository.save(newEmployee);
        return mapper.map(savedEmployee, EmployeeDTO.class);
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return repository.findAll()
                .stream().map(employee -> mapper.map(employee, EmployeeDTO.class)).toList();
    }

    @Override
    public EmployeeDTO getEmployeeByEmail(String email) {
        Employee employee = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        return mapper.map(employee, EmployeeDTO.class);
    }

    @Override
    public EmployeeDTO updateEmployeeByEmail(String email, EmployeeDTO employee) {
        Employee existingEmployee = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        if (employee.getPassword() != null && !employee.getPassword().isBlank()) {
            existingEmployee.setPassword(employee.getPassword());
        }

        existingEmployee.setName(employee.getName());
        existingEmployee.setPhone(employee.getPhone());
        existingEmployee.setBirthDate(employee.getBirthDate());
        Employee updatedEmployee = repository.save(existingEmployee);

        return mapper.map(updatedEmployee, EmployeeDTO.class);
    }

    @Override
    public void deleteEmployeeByEmail(String email) {
        Employee employee = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        repository.delete(employee);
    }

}
