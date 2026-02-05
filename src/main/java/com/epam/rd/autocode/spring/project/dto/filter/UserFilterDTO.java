package com.epam.rd.autocode.spring.project.dto.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserFilterDTO {
    private String search;
    private Integer page = 0;
    private Integer size = 5;
    private String sort = "name";
    private String dir = "asc";
    private Boolean blocked;

    public int getSafePage() {
        return page != null ? page : 0;
    }

    public int getSafeSize() {
        return size != null ? size : 10;
    }

    public String getSafeSort() {
        return sort != null ? sort : "name";
    }

    public String getSafeDir() {
        return dir != null ? dir : "asc";
    }

    public String toQueryString() {
        return Arrays.stream(this.getClass().getDeclaredFields())
                .peek(f -> f.setAccessible(true))
                .map(f -> {
                    try {
                        Object value = f.get(this);
                        if (value != null) {
                            return f.getName() + "=" + value.toString();
                        }
                    } catch (IllegalAccessException e) {
                        e.getMessage();
                    }
                    return null;
                })
                .filter(s -> s != null)
                .collect(Collectors.joining("&"));
    }
}
