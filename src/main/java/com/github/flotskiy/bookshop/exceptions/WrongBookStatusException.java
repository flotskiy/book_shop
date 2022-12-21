package com.github.flotskiy.bookshop.exceptions;

public class WrongBookStatusException extends RuntimeException {

    public WrongBookStatusException(String message) {
        super(message);
    }
}
