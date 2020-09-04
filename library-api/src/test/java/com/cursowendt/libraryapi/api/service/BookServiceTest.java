package com.cursowendt.libraryapi.api.service;

import com.cursowendt.libraryapi.exception.BusinessException;
import com.cursowendt.libraryapi.model.entity.Book;
import com.cursowendt.libraryapi.model.repository.BookRepository;
import com.cursowendt.libraryapi.service.BookService;
import com.cursowendt.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {


    BookService bookService;

    @MockBean
    BookRepository bookRepository;

    @BeforeEach
    public void setUp() {
        this.bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        //cenario
        Book book = createValidBook();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);

        Mockito.when(bookRepository.save(book))
            .thenReturn(Book.builder()
                .id(1L).isbn("123")
                .author("Fulando")
                .title("As aventuras")
                .build());

        // execucao
        Book savedBook = bookService.save(book);

        // verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulando");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
    }

    private Book createValidBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com isbn duplicado")
    void shouldNotSaveABookWithDuplicatedISBN() {
        // cenario
        Book book = createValidBook();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        // execucao
        Throwable exception = Assertions.catchThrowable(() -> bookService.save(book));

        // verificacao
        assertThat(exception)
            .isInstanceOf(BusinessException.class)
            .hasMessage("Isbn já cadastrado.");

        verify(bookRepository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTest() {
        // cenario
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);

        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        // execucao
        Optional<Book> foundBook = bookService.getById(id);

        // verificacao
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por id quando ele nao existe na base.")
    public void bookNotFoundByIdTest() {
        // cenario
        Long id = 1L;

        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // execucao
        Optional<Book> book = bookService.getById(id);

        // verificacao
        assertThat(book.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBookTest() {
        // cenario
        Book book = Book.builder().id(1L).build();

        // execucao
        assertDoesNotThrow(() -> bookService.delete(book));

        // verificacoes
        verify(bookRepository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer um erro ao tentar deletar um livro inexiste.")
    public void deleteInvalidBookTest() {
        // cenario
        Book book = new Book();

        // execucao
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.delete(book));

        // verificacoes
        verify(bookRepository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer um erro ao tentar atualizar um livro inexiste.")
    public void updateInvalidBookTest() {
        // cenario
        Book book = new Book();

        // execucao
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.update(book));

        // verificacoes
        verify(bookRepository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro.")
    void updateBookTest() {
        // cenario
        Long id = 1L;
        // livro a atualizar
        Book updatingBook = Book.builder().id(id).build();

        // simulação
        Book updatedBook = createValidBook();
        updatedBook.setId(id);

        Mockito.when(bookRepository.save(updatingBook)).thenReturn(updatedBook);

        // execucao
        Book book = bookService.update(updatingBook);

        // verificacoes
        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest() {
        // cenario
        Book book = createValidBook();

        final PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<>(lista, pageRequest, 1);
        Mockito.when(bookRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
            .thenReturn(page);

        // execucao
        Page<Book> result = bookService.find(book, pageRequest);

        // verificacao
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve obter um livro pelo isbn")
    public void getBookByIsbnTest() {
        // cenario
        String isbn = "1230";
        Mockito.when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(Book.builder().id(1L).isbn(isbn).build()));

        // execucao
        Optional<Book> book = bookService.getBookByIsbn(isbn);

        // verificacao
        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getId()).isEqualTo(1L);
        assertThat(book.get().getIsbn()).isEqualTo(isbn);

        Mockito.verify(bookRepository, times(1)).findByIsbn(isbn);
    }

}
