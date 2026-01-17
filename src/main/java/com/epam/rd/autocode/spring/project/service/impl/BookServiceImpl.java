package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {
    private BookRepository repository;
    private ModelMapper mapper;

    @Override
    public BookDTO addBook(BookDTO book) {
        Book newBook = mapper.map(book, Book.class);
        Book savedBook = repository.save(newBook);
        return mapper.map(savedBook, BookDTO.class);
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return repository.findAll()
                .stream().map(book -> mapper.map(book, BookDTO.class)).toList();
    }

    @Override
    public BookDTO getBookByName(String name) {
        Book book = repository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return mapper.map(book, BookDTO.class);
    }

    @Override
    public BookDTO updateBookByName(String name, BookDTO book) {
        Book existingBook = repository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        existingBook.setName(book.getName());
        existingBook.setGenre(book.getGenre());
        existingBook.setAgeGroup(book.getAgeGroup());
        existingBook.setPrice(book.getPrice());
        existingBook.setPublicationDate(book.getPublicationDate());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPages(book.getPages());
        existingBook.setDescription(book.getDescription());
        existingBook.setCharacteristics(book.getCharacteristics());
        existingBook.setLanguage(book.getLanguage());
        Book updatedBook = repository.save(existingBook);

        return mapper.map(updatedBook, BookDTO.class);
    }

    @Override
    public void deleteBookByName(String name) {
        Book book = repository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        repository.delete(book);
    }
}
