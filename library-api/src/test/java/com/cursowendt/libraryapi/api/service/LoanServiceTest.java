package com.cursowendt.libraryapi.api.service;

import com.cursowendt.libraryapi.api.dto.LoanFilterDTO;
import com.cursowendt.libraryapi.exception.BusinessException;
import com.cursowendt.libraryapi.model.entity.Book;
import com.cursowendt.libraryapi.model.entity.Loan;
import com.cursowendt.libraryapi.model.repository.LoanRepository;
import com.cursowendt.libraryapi.service.LoanService;
import com.cursowendt.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

        when(loanRepositoty.existsByBookAndNotReturned(book)).thenReturn(false);
        when(loanRepositoty.save(savingLoan)).thenReturn(savedLoan);

        // execucao
        Loan loan = loanService.save(savingLoan);

        // verificacao
        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }

    @Test
    @DisplayName("Deve lençar um erro de negócio ao salvar um empréstimo com um livro já emprestado")
    public void loanedBookSavedTest() {
        // cenario
        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";

        Loan savingLoan = Loan.builder()
            .book(book)
            .customer(customer)
            .loanDate(LocalDate.now())
            .build();

        when(loanRepositoty.existsByBookAndNotReturned(book)).thenReturn(true);

        // execucao
        Throwable exception = catchThrowable(() -> loanService.save(savingLoan));

        // verificacao
        assertThat(exception)
            .isInstanceOf(BusinessException.class)
            .hasMessage("Book already loaned");

        verify(loanRepositoty, never()).save(savingLoan);
    }

    @Test
    @DisplayName("Deve obter as informações de um empréstimo pelo ID")
    public void getLoanDetailsTest() {
        // cenario - given
        Long id = 1L;

        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(loanRepositoty.findById(id)).thenReturn(Optional.of(loan));

        // execucao - when
        Optional<Loan> result = loanService.getById(id);

        // verificacao
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(loanRepositoty).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar um empréstimo")
    public void updateLoanTest() {
        // given
        Loan loan = createLoan();
        loan.setId(1L);
        loan.setReturned(true);

        Mockito.when(loanRepositoty.save(loan)).thenReturn(loan);

        // when
        Loan updatedLoan = loanService.update(loan);

        // then
        assertThat(updatedLoan.getReturned()).isTrue();

        verify(loanRepositoty).save(loan);
    }

    @Test
    @DisplayName("Deve filtrar emprestimos pelas propriedades")
    public void findLoanTest() {
        // cenario
        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder()
            .customer("Fulano")
            .isbn("321")
            .build();

        Loan loan = createLoan();
        loan.setId(1L);

        final PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> lista = Arrays.asList(loan);
        Page<Loan> page = new PageImpl<>(lista, pageRequest, lista.size());
        Mockito.when(loanRepositoty.findByBookIsbnOrCustomer(Mockito.anyString(), Mockito.anyString() , Mockito.any(PageRequest.class)))
            .thenReturn(page);

        // execucao
        Page<Loan> result = loanService.find(loanFilterDTO, pageRequest);

        // verificacao
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    public static Loan createLoan() {
        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";

        return Loan.builder()
            .book(book)
            .customer(customer)
            .loanDate(LocalDate.now())
            .build();
    }

}
