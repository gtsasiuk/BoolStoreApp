package com.epam.rd.autocode.spring.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private List<BookItemDTO> items = new ArrayList<>();

    public void addBook(String bookName) {
        for (BookItemDTO item : items) {
            if (item.getBookName().equals(bookName)) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        items.add(new BookItemDTO(bookName, 1));
    }

    public void removeBook(String bookName) {
        items.removeIf(i -> i.getBookName().equals(bookName));
    }

    public void updateQuantity(String bookName, int qty) {
        items.stream()
                .filter(i -> i.getBookName().equals(bookName))
                .findFirst()
                .ifPresent(i -> i.setQuantity(qty));
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}

