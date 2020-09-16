package com.cursowendt.libraryapi.model.repository;

import com.cursowendt.libraryapi.model.entity.Book;
import com.cursowendt.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static com.cursowendt.libraryapi.model.repository.BookRepositoryTest.createNewBook;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve verificar se existe o empréstimo não devolvido para o livro")
    public void existsByBookAndNotReturnedTest() {
        // given - cenario
        Loan loan = createAndPersistLoan(LocalDate.now());
        Book book = loan.getBook();

        // when - execucao
        Boolean exists = loanRepository.existsByBookAndNotReturned(book);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve buscar o emprestimo pelo isbn do livro ou customer")
    public void findByBookIsbnOrCustomer() {
        // cenario
        Loan loan = createAndPersistLoan(LocalDate.now());

        //execucao
        Page<Loan> result = loanRepository.findByBookIsbnOrCustomer("123", "Fulando", PageRequest.of(0, 10));

        // verificacao
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve obter empréstimos cuja data do empréstimo for menor ou igual a três dias atrás e não retornados")
    public void findByLoanDateLessThanAndNotReturned() {
        // given
        Loan loan = createAndPersistLoan(LocalDate.now().minusDays(5));

        // when
        List<Loan> result = loanRepository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        // then
        assertThat(result).hasSize(1).contains(loan);
    }

    @Test
    @DisplayName("Deve retornar vazio quando houver empréstimos atrasados")
    public void notFindByLoanDateLessThanAndNotReturned() {
        // given
        Loan loan = createAndPersistLoan(LocalDate.now());

        // when
        List<Loan> result = loanRepository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        // then
        assertThat(result).isEmpty();
    }

    private Loan createAndPersistLoan(final LocalDate loanDate) {
        final Book book = createNewBook("123");
        entityManager.persist(book);

        final Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(loanDate).build();
        entityManager.persist(loan);

        return loan;
    }

}
