package com.Sistema.PrestamosBancarios.exception;

public class InvalidLoanStatusException extends RuntimeException {

    public InvalidLoanStatusException(String message) {
        super(message);
    }
}
