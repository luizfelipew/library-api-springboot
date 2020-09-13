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
        Loan loan = createAndPersistLoan();
        Book book = loan.getBook();

        // when - execucao
        Boolean exists = loanRepository.existsByBookAndNotReturned(book);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve buscar o emprestimo pelo isbn do livro ou customer")
    public void findByBookIsbnOrCustomer() {
        // cenario
        Loan loan = createAndPersistLoan();

        //execucao
        Page<Loan> result = loanRepository.findByBookIsbnOrCustomer("123", "Fulando", PageRequest.of(0, 10));

        // verificacao
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    private Loan createAndPersistLoan() {
        final Book book = createNewBook("123");
        entityManager.persist(book);

        final Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        return loan;
    }

}
