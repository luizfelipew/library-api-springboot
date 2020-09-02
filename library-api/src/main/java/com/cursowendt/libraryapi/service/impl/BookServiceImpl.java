package com.cursowendt.libraryapi.service.impl;

import com.cursowendt.libraryapi.model.entity.Book;
import com.cursowendt.libraryapi.model.repository.BookRepository;
import com.cursowendt.libraryapi.service.BookService;
import com.cursowendt.libraryapi.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    public BookServiceImpl(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(final Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isbn já cadastrado.");
        }
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> getById(final Long id) {
        return this.bookRepository.findById(id);
    }

    @Override
    public void delete(final Book book) {
        if (Objects.isNull(book) || Objects.isNull(book.getId())) {
            throw new IllegalArgumentException("Book id can't be null.");
        }
        this.bookRepository.delete(book);
    }

    @Override
    public Book update(final Book book) {
        if (Objects.isNull(book) || Objects.isNull(book.getId())) {
            throw new IllegalArgumentException("Book id can't be null.");
        }
        return this.bookRepository.save(book);
    }

    @Override
    public Page<Book> find(final Book filter, final Pageable pageRequest) {
        final Example<Book> example = Example.of(filter,
            ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return bookRepository.findAll(example, pageRequest);
    }

    @Override
    public Optional<Book> getBookByIsbn(final String isbn) {
        return null;
    }

}
