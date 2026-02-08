package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.user.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.filter.UserFilterDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import com.epam.rd.autocode.spring.project.specification.EmployeeSpecs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository repository;
    private ModelMapper mapper;

    @Override
    public EmployeeDTO addEmployee(EmployeeDTO employee) {
        log.info("Creating employee email={}", employee.getEmail());
        if (repository.existsByEmail(employee.getEmail())) {
            log.warn("Employee already exists email={}", employee.getEmail());
            throw new AlreadyExistException("Employee already exists");
        }
        Employee newEmployee = mapper.map(employee, Employee.class);
        newEmployee.setBlocked(false);
        Employee savedEmployee = repository.save(newEmployee);
        log.info("Employee created successfully email={}", employee.getEmail());
        return mapper.map(savedEmployee, EmployeeDTO.class);
    }

    @Override
    public void toggleBlockByEmail(String email) {
        log.warn("Toggling block status for employee email={}", email);
        Employee employee = repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Employee not found email={}", email);
                    return new NotFoundException("Employee not found");
                });

        employee.setBlocked(!employee.getBlocked());
        repository.save(employee);

        log.info("Employee block status changed email={} blocked={}",
                email, employee.getBlocked());
    }

    @Override
    public Page<EmployeeDTO> getAllEmployees(UserFilterDTO filter) {
        log.info("Fetching employees page={} size={} search={} blocked={}",
                filter.getSafePage(),
                filter.getSafeSize(),
                filter.getSearch(),
                filter.getBlocked());

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
        log.info("Fetching employee email={}", email);
        Employee employee = repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Employee not found email={}", email);
                    return new NotFoundException("Employee not found");
                });
        return mapper.map(employee, EmployeeDTO.class);
    }

    @Override
    public EmployeeDTO updateEmployeeByEmail(String email, EmployeeDTO employee) {
        log.info("Updating employee email={}", email);
        Employee existingEmployee = repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Employee not found email={}", email);
                    return new NotFoundException("Employee not found");
                });

        if (employee.getPassword() != null && !employee.getPassword().isBlank()) {
            existingEmployee.setPassword(employee.getPassword());
        }

        existingEmployee.setName(employee.getName());
        existingEmployee.setPhone(employee.getPhone());
        existingEmployee.setBirthDate(employee.getBirthDate());
        Employee updatedEmployee = repository.save(existingEmployee);
        log.info("Employee updated successfully email={}", email);

        return mapper.map(updatedEmployee, EmployeeDTO.class);
    }

    @Override
    public void deleteEmployeeByEmail(String email) {
        log.warn("Soft deleting employee email={}", email);
        Employee employee = repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        employee.setBlocked(true);
        repository.save(employee);
    }

}
