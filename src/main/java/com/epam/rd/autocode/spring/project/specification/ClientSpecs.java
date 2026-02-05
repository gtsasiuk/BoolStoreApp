package com.epam.rd.autocode.spring.project.specification;

import com.epam.rd.autocode.spring.project.model.Client;
import org.springframework.data.jpa.domain.Specification;

public final class ClientSpecs {
    public static Specification<Client> nameOrEmailContains(String keyword) {
        return UserSpecs.nameLike(keyword, Client.class)
                .or(UserSpecs.emailLike(keyword, Client.class));
    }

    public static Specification<Client> hasBlockedStatus(Boolean blocked) {
        return UserSpecs.blocked(blocked, Client.class);
    }
}
