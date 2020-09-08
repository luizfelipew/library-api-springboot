package com.cursowendt.libraryapi.api.service;

import com.cursowendt.libraryapi.service.impl.LoanServiceImpl;
import com.cursowendt.libraryapi.model.entity.Book;
import com.cursowendt.libraryapi.model.entity.Loan;
import com.cursowendt.libraryapi.model.repository.LoanRepository;
import com.cursowendt.libraryapi.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService loanService;

    @MockBean
    LoanRepository loanRepositoty;

    @BeforeEach
    public void setUp() {
        this.loanService = new LoanServiceImpl(loanRepositoty);

    }

    @Test
    @DisplayName("Deve salvar um empréstimo")
    public void saveLoanTest() {
        // cenario
        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";

        Loan savingLoan = Loan.builder()
            .book(book)
            .customer(customer)
            .loanDate(LocalDate.now())
            .build();

        Loan savedLoan = Loan.builder()
            .id(1L)
            .book(book)
            .customer(customer)
            .loanDate(LocalDate.now())
            .build();

        when(loanRepositoty.save(savingLoan)).thenReturn(savedLoan);

        // execucao
        Loan loan = loanService.save(savingLoan);

        // verificacao
        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }

}
