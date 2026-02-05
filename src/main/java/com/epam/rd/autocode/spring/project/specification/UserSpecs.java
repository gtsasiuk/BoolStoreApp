package com.epam.rd.autocode.spring.project.specification;

import org.springframework.data.jpa.domain.Specification;

public final class UserSpecs {
    public static <T> Specification<T> nameLike(String keyword, Class<T> type) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }

    public static <T> Specification<T> emailLike(String keyword, Class<T> type) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("email")), "%" + keyword.toLowerCase() + "%");
    }

    public static <T> Specification<T> blocked(Boolean blocked, Class<T> type) {
        return (root, query, cb) -> cb.equal(root.get("blocked"), blocked);
    }
}
