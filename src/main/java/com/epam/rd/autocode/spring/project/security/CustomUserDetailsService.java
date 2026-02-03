package com.epam.rd.autocode.spring.project.security;

import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {

        var clientOpt = clientRepository.findByEmail(email);
        if (clientOpt.isPresent()) {
            var client = clientOpt.get();
            return new CustomUserDetails(client, client.getBlocked());
        }

        var employeeOpt = employeeRepository.findByEmail(email);
        if (employeeOpt.isPresent()) {
            var employee = employeeOpt.get();
            return new CustomUserDetails(employee, employee.getBlocked());
        }

        throw new UsernameNotFoundException("User not found: " + email);
    }

}
