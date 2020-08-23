package com.cursowendt.libraryapi.api.service.impl;

import com.cursowendt.libraryapi.api.model.entity.Book;
import com.cursowendt.libraryapi.api.service.BookService;
import com.cursowendt.libraryapi.api.model.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    public BookServiceImpl(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(final Book book) {
        return bookRepository.save(book);
    }

}
