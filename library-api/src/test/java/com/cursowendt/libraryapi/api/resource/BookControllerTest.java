package com.cursowendt.libraryapi.api.resource;

import com.cursowendt.libraryapi.api.dto.BookDTO;
import com.cursowendt.libraryapi.model.entity.Book;
import com.cursowendt.libraryapi.service.BookService;
import com.cursowendt.libraryapi.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createBookTest() throws Exception {

        BookDTO bookDTO = createNewBook();
        Book savedBook = Book.builder().id(10L).author("Artur").title("As aventuras").isbn("001").build();
        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(savedBook);
        String json = new ObjectMapper().writeValueAsString(bookDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .post(BOOK_API)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(json);

        mockMvc
            .perform(request)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").value(10L))
            .andExpect(jsonPath("title").value(bookDTO.getTitle()))
            .andExpect(jsonPath("author").value(bookDTO.getAuthor()))
            .andExpect(jsonPath("isbn").value(bookDTO.getIsbn()));
    }


    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criação do livro")
    public void createInvalidBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .post(BOOK_API)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(json);

        mockMvc
            .perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outros.")
    public void createBookWithDuplicatedIsbn() throws Exception {
        BookDTO bookDTO = createNewBook();
        String json = new ObjectMapper().writeValueAsString(bookDTO);
        String mensagemError = "Isbn já cadastrado.";
        BDDMockito.given(bookService.save(Mockito.any()))
            .willThrow(new BusinessException(mensagemError));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .post(BOOK_API)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(json);

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("errors", hasSize(1)))
            .andExpect(jsonPath("errors[0]").value(mensagemError));
    }

    @Test
    @DisplayName("Deve obter informacoes de um livro.")
    public void getBookDetailTest() throws Exception {
        // cenario (given)
        Long id = 1L;

        Book book = Book.builder()
            .id(id)
            .title(createNewBook().getTitle())
            .author(createNewBook().getAuthor())
            .isbn(createNewBook().getIsbn())
            .build();
        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(book));

        // execucao (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .get(BOOK_API.concat("/" + id))
            .accept(MediaType.APPLICATION_JSON);

        // verificacao (then)
        mockMvc
            .perform(request)
            .andExpect(status().isOk())
            .andExpect(jsonPath("id").value(id))
            .andExpect(jsonPath("title").value(createNewBook().getTitle()))
            .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
            .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado não existir")
    public void bookNotFoundTest() throws Exception {

        // cenario
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        // execucao (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .get(BOOK_API.concat("/" + 1))
            .accept(MediaType.APPLICATION_JSON);

        // verificacao (then)
        mockMvc
            .perform(request)
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() throws Exception {
        // cenario
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1L).build()));

        // execucao (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .delete(BOOK_API.concat("/" + 1));

        // verificacao
        mockMvc
            .perform(request)
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar not found quando não encontrar o livro para deletar")
    public void deleteInexistentBookTest() throws Exception {
        // cenario
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        // execucao (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .delete(BOOK_API.concat("/" + 1));

        // verificacao
        mockMvc
            .perform(request)
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() throws Exception {
        // given
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        Book updatingBook = Book.builder().id(1L).title("some title").author("some author").isbn("321").build();
        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(updatingBook));

        Book updateBook = Book.builder().id(id).author("Artur").title("As aventuras").isbn("321").build();
        BDDMockito.given(bookService.update(updatingBook)).willReturn(updateBook);

        // when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .put(BOOK_API.concat("/" + 1))
            .content(json)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(request)
            .andExpect(status().isOk())
            .andExpect(jsonPath("id").value(id))
            .andExpect(jsonPath("title").value(createNewBook().getTitle()))
            .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
            .andExpect(jsonPath("isbn").value("321"));
    }

    @Test
    @DisplayName("Deve retornar um 404 ao tentar atualizar um livro inexistente")
    public void updateInexistentBookTest() throws Exception {
        // given
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        // when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .put(BOOK_API.concat("/" + 1))
            .content(json)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(request)
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve filtrar livros.")
    public void findBooksTest() throws Exception {
        // given
        Long id  = 1L;
        Book book = Book.builder().id(id)
            .author(createNewBook().getAuthor())
            .title(createNewBook().getTitle())
            .isbn(createNewBook().getIsbn())
            .build();

        BDDMockito.given(bookService.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
            .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());

        // when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .get(BOOK_API.concat(queryString))
            .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc
            .perform(request)
            .andExpect(status().isOk())
            .andExpect(jsonPath("content", Matchers.hasSize(1)))
            .andExpect(jsonPath("totalElements").value(1))
            .andExpect(jsonPath("pageable.pageSize").value(100))
            .andExpect(jsonPath("pageable.pageNumber").value(0));


    }

    private BookDTO createNewBook() {
        return BookDTO.builder().author("Artur").title("As aventuras").isbn("001").build();
    }

}
