package com.cursowendt.libraryapi.api.service.impl;

import com.cursowendt.libraryapi.api.model.entity.Book;
import com.cursowendt.libraryapi.api.service.BookService;
import com.cursowendt.libraryapi.api.model.repository.BookRepository;
import com.cursowendt.libraryapi.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            throw  new BusinessException("Isbn já cadastrado.");
        }
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> getById(final Long id) {
        return Optional.empty();
    }

    @Override
    public void delete(final Book book) {

    }

}
