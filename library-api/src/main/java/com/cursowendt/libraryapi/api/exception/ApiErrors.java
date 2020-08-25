package com.cursowendt.libraryapi.api.exception;

import com.cursowendt.libraryapi.exception.BusinessException;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiErrors {

    private List<String> errors;

    public ApiErrors(final BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors()
            .forEach(error -> this.errors.add(error.getDefaultMessage()));
    }

    public ApiErrors(final BusinessException ex){
        this.errors = Arrays.asList(ex.getMessage());
    }

    public List<String> getErrors() {
        return errors;
    }

}
