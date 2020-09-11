package com.cursowendt.libraryapi.api.resource;

import com.cursowendt.libraryapi.api.dto.LoanDTO;
import com.cursowendt.libraryapi.api.dto.LoanFilterDTO;
import com.cursowendt.libraryapi.api.dto.ReturnedLoanDTO;
import com.cursowendt.libraryapi.api.service.LoanServiceTest;
import com.cursowendt.libraryapi.exception.BusinessException;
import com.cursowendt.libraryapi.model.entity.Book;
import com.cursowendt.libraryapi.model.entity.Loan;
import com.cursowendt.libraryapi.service.BookService;
import com.cursowendt.libraryapi.service.LoanService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;


    @Test
    @DisplayName("Deve realizar um emprestimo.")
    public void createLoanTest() throws Exception {

        // given - cenario
        LoanDTO loanDTO = LoanDTO.builder().isbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(loanDTO);

        Book book = Book.builder().id(1L).isbn("123").build();

        BDDMockito.given(bookService.getBookByIsbn("123"))
            .willReturn(Optional.of(book));

        final Loan loan = Loan.builder().id(1L).customer("Fulano").book(book).loanDate(LocalDate.now()).build();
        BDDMockito.given(loanService.save(BDDMockito.any(Loan.class))).willReturn(loan);

        // when - execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

        // then - verificacao
        mockMvc.perform(request)
            .andExpect(status().isCreated())
            .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro inexistente.")
    public void invalidIsbnCreateLoanTest() throws Exception {
        // given
        LoanDTO loanDTO = LoanDTO.builder().isbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(loanDTO);

        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.empty());

        // when - execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

        // then - verificacao
        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("errors", Matchers.hasSize(1)))
            .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro emprestado.")
    public void loanedBookErrorOnCreateLoanTest() throws Exception {
        // given - cenario
        LoanDTO loanDTO = LoanDTO.builder().isbn("123").customer("Fulano").build();
        String json = new ObjectMapper().writeValueAsString(loanDTO);

        Book book = Book.builder().id(1L).isbn("123").build();
        BDDMockito.given(bookService.getBookByIsbn("123"))
            .willReturn(Optional.of(book));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
            .willThrow(new BusinessException("Book already loaned"));

        // when - execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

        // then - verificacao
        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("errors", Matchers.hasSize(1)))
            .andExpect(jsonPath("errors[0]").value("Book already loaned"));
    }

    @Test
    @DisplayName("Deve retornar um livro")
    public void returnBookTest() throws Exception {
        // cenario ( returned: true )
        final ReturnedLoanDTO returnedLoanDTO = ReturnedLoanDTO.builder().returned(true).build();
        Loan loan = Loan.builder().id(1L).build();
        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.of(loan));

        final String json = new ObjectMapper().writeValueAsString(returnedLoanDTO);

        mockMvc.perform(
            patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                       ).andExpect(status().isOk());

        Mockito.verify(loanService, Mockito.times(1)).update(loan);
    }

    @Test
    @DisplayName("Deve retornar 404 quando retornar um livro inexistente")
    public void returnInexistentBookTest() throws Exception {
        // cenario ( returned: true )
        final ReturnedLoanDTO returnedLoanDTO = ReturnedLoanDTO.builder().returned(true).build();
        final String json = new ObjectMapper().writeValueAsString(returnedLoanDTO);

        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.empty());


        mockMvc.perform(
            patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                       ).andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve filtrar empréstimos")
    public void findLoanTest() throws Exception {
        // given
        Long id  = 1L;

        Loan loan = LoanServiceTest.createLoan();
        loan.setId(id);
        Book book = Book.builder().id(1L).isbn("321").build();
        loan.setBook(book);

        BDDMockito.given(loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)))
            .willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 10), 1));

        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=100", book.getIsbn(), loan.getCustomer());

        // when
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .get(LOAN_API.concat(queryString))
            .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc
            .perform(request)
            .andExpect(status().isOk())
            .andExpect(jsonPath("content", Matchers.hasSize(1)))
            .andExpect(jsonPath("totalElements").value(1))
            .andExpect(jsonPath("pageable.pageSize").value(10))
            .andExpect(jsonPath("pageable.pageNumber").value(0));


    }

}
