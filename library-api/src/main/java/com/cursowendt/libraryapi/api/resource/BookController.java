package com.cursowendt.libraryapi.api.resource;

import com.cursowendt.libraryapi.api.dto.BookDTO;
import com.cursowendt.libraryapi.api.model.entity.Book;
import com.cursowendt.libraryapi.api.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;

    public BookController(final BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody BookDTO bookDTO){
        Book entity = Book.builder()
            .author(bookDTO.getAuthor())
            .title(bookDTO.getTitle())
            .isbn(bookDTO.getIsbn())
            .build();

        entity = bookService.save(entity);

        return BookDTO.builder()
            .id(entity.getId())
            .author(entity.getAuthor())
            .title(entity.getTitle())
            .isbn(entity.getIsbn())
            .build();
    }
}
