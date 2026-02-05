package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.filter.UserFilterDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import com.epam.rd.autocode.spring.project.specification.EmployeeSpecs;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
        newEmployee.setBlocked(false);
        Employee savedEmployee = repository.save(newEmployee);
        return mapper.map(savedEmployee, EmployeeDTO.class);
    }

    @Override
    public void toggleBlockByEmail(String email) {
        Employee employee = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        employee.setBlocked(!employee.getBlocked());
        repository.save(employee);
    }

    @Override
    public Page<EmployeeDTO> getAllEmployees(UserFilterDTO filter) {
        Pageable pageable = PageRequest.of(
                filter.getSafePage(),
                filter.getSafeSize(),
                Sort.by(Sort.Direction.fromString(filter.getSafeDir()), filter.getSafeSort())
        );

        Specification<Employee> spec = Specification.where(null);

        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            spec = spec.and(EmployeeSpecs.nameOrEmailContains(filter.getSearch()));
        }

        if (filter.getBlocked() != null) {
            spec = spec.and(EmployeeSpecs.hasBlockedStatus(filter.getBlocked()));
        }

        return repository.findAll(spec, pageable)
                .map(employee -> mapper.map(employee, EmployeeDTO.class));
    }

    @Override
    public EmployeeDTO getEmployeeByEmail(String email) {
        Employee employee = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found"));
        return mapper.map(employee, EmployeeDTO.class);
    }

    @Override
    public EmployeeDTO updateEmployeeByEmail(String email, EmployeeDTO employee) {
        Employee existingEmployee = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

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
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        employee.setBlocked(true);
        repository.save(employee);
    }

}
