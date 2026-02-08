package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.filter.BookFilterDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.specification.BookSpecs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {
    private BookRepository repository;
    private ModelMapper mapper;

    @Override
    public BookDTO addBook(BookDTO book) {
        log.info("Creating book name={}", book.getName());
        if (repository.existsByName(book.getName())) {
            log.warn("Book already exists name={}", book.getName());
            throw new AlreadyExistException("Book already exists");
        }
        Book newBook = mapper.map(book, Book.class);
        newBook.setActive(true);
        Book savedBook = repository.save(newBook);
        log.info("Book created successfully name={}", book.getName());
        return mapper.map(savedBook, BookDTO.class);
    }

    @Override
    public Page<BookDTO> getAllBooks(BookFilterDTO filter, Boolean isEmployee) {
        log.debug("Fetching books page={} size={} search={} ageGroup={} language={} isEmployee={}",
                filter.getSafePage(),
                filter.getSafeSize(),
                filter.getSearch(),
                filter.getAgeGroup(),
                filter.getLanguage(),
                isEmployee);

        Pageable pageable = PageRequest.of(
                filter.getSafePage(),
                filter.getSafeSize(),
                Sort.by(Sort.Direction.fromString(filter.getSafeDir()), filter.getSafeSort())
        );

        Specification<Book> spec = Specification.where(null);

        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            spec = spec.and(BookSpecs.nameContains(filter.getSearch()));
        }

        if (filter.getAgeGroup() != null) {
            spec = spec.and(BookSpecs.hasAgeGroup(filter.getAgeGroup()));
        }

        if (filter.getLanguage() != null) {
            spec = spec.and(BookSpecs.hasLanguage(filter.getLanguage()));
        }

        if (!isEmployee) {
            spec = spec.and(BookSpecs.isActive());
        } else if (filter.getActive() != null) {
            spec = spec.and(BookSpecs.hasActive(filter.getActive()));
        }

        return repository.findAll(spec, pageable)
                .map(book -> mapper.map(book, BookDTO.class));
    }

    @Override
    public BookDTO getBookByName(String name) {
        Book book = repository.findByName(name)
                .orElseThrow(() -> {
                    log.warn("Book not found name={}", name);
                    return new NotFoundException("Book not found");
                });
        return mapper.map(book, BookDTO.class);
    }

    @Override
    public BookDTO updateBookByName(String name, BookDTO book) {
        log.info("Fetching book by name={}", name);
        Book existingBook = repository.findByName(name)
                .orElseThrow(() -> {
                    log.warn("Book not found name={}", name);
                    return new NotFoundException("Book not found");
                });

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
    public void toggleBookActive(String name) {
        log.warn("Toggling book active status name={}", name);
        Book book = repository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Book not found"));

        book.setActive(!book.getActive());
        repository.save(book);
        log.info("Book active status changed name={} active={}",
                name, book.getActive());
    }
}
