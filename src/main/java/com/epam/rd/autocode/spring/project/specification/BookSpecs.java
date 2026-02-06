package com.epam.rd.autocode.spring.project.specification;

import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import org.springframework.data.jpa.domain.Specification;

public final class BookSpecs {
    public static Specification<Book> nameContains(String text) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("name")),
                        "%" + text.toLowerCase() + "%"
                );
    }

    public static Specification<Book> hasAgeGroup(AgeGroup ageGroup) {
        return (root, query, cb) ->
                cb.equal(root.get("ageGroup"), ageGroup);
    }

    public static Specification<Book> hasLanguage(Language language) {
        return (root, query, cb) ->
                cb.equal(root.get("language"), language);
    }

    public static Specification<Book> isActive() {
        return (root, query, cb) ->
                cb.isTrue(root.get("active"));
    }

    public static Specification<Book> hasActive(boolean active) {
        return (root, query, cb) ->
                cb.equal(root.get("active"), active);
    }
}
