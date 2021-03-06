package com.cursowendt.libraryapi.service.impl;

import com.cursowendt.libraryapi.api.dto.LoanFilterDTO;
import com.cursowendt.libraryapi.exception.BusinessException;
import com.cursowendt.libraryapi.model.entity.Book;
import com.cursowendt.libraryapi.model.entity.Loan;
import com.cursowendt.libraryapi.model.repository.LoanRepository;
import com.cursowendt.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    public LoanServiceImpl(final LoanRepository loanRepositoty) {
        this.loanRepository = loanRepositoty;
    }

    @Override
    public Loan save(final Loan loan) {
        if(loanRepository.existsByBookAndNotReturned(loan.getBook())){
            throw new BusinessException("Book already loaned");
        }
        return loanRepository.save(loan);
    }

    @Override
    public Optional<Loan> getById(final Long id) {
        return loanRepository.findById(id);
    }

    @Override
    public Loan update(final Loan loan) {
        return loanRepository.save(loan);
    }

    @Override
    public Page<Loan> find(final LoanFilterDTO filterDTO, final Pageable pageable) {
        return loanRepository.findByBookIsbnOrCustomer(filterDTO.getIsbn(), filterDTO.getCustomer(), pageable);
    }

    @Override
    public Page<Loan> getLoansByBook(final Book book, final Pageable pageable) {
        return loanRepository.findByBook(book, pageable);
    }

    @Override
    public List<Loan> getAllLateLoans() {
        final Integer loansDays = 4;
        LocalDate threeDaysAgo = LocalDate.now().minusDays(loansDays);
        return loanRepository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);
    }

}
