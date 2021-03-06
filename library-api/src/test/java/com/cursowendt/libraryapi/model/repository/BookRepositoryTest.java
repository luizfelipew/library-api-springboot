package com.cursowendt.libraryapi.model.repository;

import com.cursowendt.libraryapi.model.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
    public void returnTrueWhenIsbnExists() {
        //cenario
        String isbn = "123";
        Book book = createNewBook(isbn);
        entityManager.persist(book);

        //execucao
        Boolean exists = bookRepository.existsByIsbn(isbn);

        // verificacao
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando não existir um livro na base com o isbn informado")
    public void returnFalseWhenIsbnDoesntExists() {
        //cenario
        String isbn = "123";

        //execucao
        Boolean exists = bookRepository.existsByIsbn(isbn);

        // verificacao
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve obter um livro por id.")
    public void findByIdTest() {
        // cenario
        Book book = createNewBook("123");
        entityManager.persist(book);

        // execucao
        Optional<Book> foundBook = bookRepository.findById(book.getId());

        // verificacao
        assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um livro.")
    public void saveBookTest() {
        // cenario
        Book book = createNewBook("123");

        // execucao
        Book savedBook = bookRepository.save(book);

        // verificacoes
        assertThat(savedBook).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBookTest() {
        // cenario
        Book book = createNewBook("123");
        entityManager.persist(book);

        // execucao
        Book foundBook = entityManager.find(Book.class, book.getId());
        bookRepository.delete(foundBook);

        Book deletedBook = entityManager.find(Book.class, book.getId());
        // verificacoes
        assertThat(deletedBook).isNull();

    }

    public static Book createNewBook(final String isbn) {
        return Book.builder()
            .title("Aventuras")
            .author("Fulano")
            .isbn(isbn)
            .build();
    }

}
