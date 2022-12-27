package com.github.flotskiy.bookshop.exceptions;

public class AttemptsNumberExceededException extends ConfirmationException {

    public AttemptsNumberExceededException(String message) {
        super(message);
    }
}
