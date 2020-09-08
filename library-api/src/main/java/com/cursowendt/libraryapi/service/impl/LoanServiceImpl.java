package com.cursowendt.libraryapi.service.impl;

import com.cursowendt.libraryapi.model.entity.Loan;
import com.cursowendt.libraryapi.model.repository.LoanRepository;
import com.cursowendt.libraryapi.service.LoanService;

public class LoanServiceImpl implements LoanService {

    private LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Loan save(final Loan loan) {
        return loanRepository.save(loan);
    }

}
