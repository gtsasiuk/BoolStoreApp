package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.dto.filter.UserFilterDTO;
import org.springframework.data.domain.Page;

public interface EmployeeService {

    Page<EmployeeDTO> getAllEmployees(UserFilterDTO filter);

    EmployeeDTO getEmployeeByEmail(String email);

    EmployeeDTO updateEmployeeByEmail(String email, EmployeeDTO employee);

    void deleteEmployeeByEmail(String email);

    EmployeeDTO addEmployee(EmployeeDTO employee);

    void toggleBlockByEmail(String email);
}
