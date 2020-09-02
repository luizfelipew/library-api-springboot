package com.cursowendt.libraryapi.api.resource;

import com.cursowendt.libraryapi.api.dto.LoanDTO;
import com.cursowendt.libraryapi.service.BookService;
import com.cursowendt.libraryapi.model.entity.Book;
import com.cursowendt.libraryapi.model.entity.Loan;
import com.cursowendt.libraryapi.service.LoanService;
import com.cursowendt.libraryapi.service.impl.BookServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Optional;

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
            .andExpect(jsonPath("id").value(1L));
    }

}
