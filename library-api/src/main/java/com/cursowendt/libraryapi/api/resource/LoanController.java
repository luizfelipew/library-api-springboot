package com.cursowendt.libraryapi.api.resource;

import com.cursowendt.libraryapi.api.dto.BookDTO;
import com.cursowendt.libraryapi.api.dto.LoanDTO;
import com.cursowendt.libraryapi.api.dto.LoanFilterDTO;
import com.cursowendt.libraryapi.api.dto.ReturnedLoanDTO;
import com.cursowendt.libraryapi.model.entity.Book;
import com.cursowendt.libraryapi.model.entity.Loan;
import com.cursowendt.libraryapi.service.BookService;
import com.cursowendt.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO loanDTO) {
        Book book = bookService.getBookByIsbn(loanDTO.getIsbn())
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));

        Loan entity = Loan.builder()
            .book(book)
            .customer(loanDTO.getCustomer())
            .loanDate(LocalDate.now())
            .build();

        entity = loanService.save(entity);

        return entity.getId();
    }

    @PatchMapping("/{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO returnedLoanDTO) {
        Loan loan = loanService.getById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        loan.setReturned(returnedLoanDTO.getReturned());
        loanService.update(loan);
    }

    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO loanFilterDTO, Pageable pageable) {
        Page<Loan> result = loanService.find(loanFilterDTO, pageable);
        List<LoanDTO> loans = result.getContent()
            .stream()
            .map(entity -> {
                Book book = entity.getBook();
                BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
                loanDTO.setBook(bookDTO);
                return loanDTO;
            })
            .collect(Collectors.toList());
        return new PageImpl<LoanDTO>(loans, pageable, result.getTotalElements());
    }

}
