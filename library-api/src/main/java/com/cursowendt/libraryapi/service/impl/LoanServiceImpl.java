package com.cursowendt.libraryapi.service.impl;

import com.cursowendt.libraryapi.exception.BusinessException;
import com.cursowendt.libraryapi.model.entity.Loan;
import com.cursowendt.libraryapi.model.repository.LoanRepository;
import com.cursowendt.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

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
        return null;
    }

}
