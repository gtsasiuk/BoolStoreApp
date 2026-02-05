package com.epam.rd.autocode.spring.project.specification;

import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import org.springframework.data.jpa.domain.Specification;

public final class EmployeeSpecs {

    public static Specification<Employee> nameOrEmailContains(String keyword) {
        return UserSpecs.nameLike(keyword, Employee.class)
                .or(UserSpecs.emailLike(keyword, Employee.class));
    }

    public static Specification<Employee> hasBlockedStatus(Boolean blocked) {
        return UserSpecs.blocked(blocked, Employee.class);
    }
}

