package com.epam.rd.autocode.spring.project.specification;

import com.epam.rd.autocode.spring.project.model.Order;
import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class OrderSpecs {

    public static Specification<Order> clientEmailLike(String keyword) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("client").get("email")),
                        "%" + keyword.toLowerCase() + "%"
                );
    }


    public static Specification<Order> employeeEmailLike(String keyword) {
        return (root, query, cb) -> {
            var employeeJoin = root.join("employee", JoinType.LEFT);
            return cb.like(
                    cb.lower(employeeJoin.get("email")),
                    "%" + keyword.toLowerCase() + "%"
            );
        };
    }


    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("orderStatus"), status);
    }
}
