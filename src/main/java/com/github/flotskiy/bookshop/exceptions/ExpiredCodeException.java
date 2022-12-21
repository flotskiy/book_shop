package com.github.flotskiy.bookshop.exceptions;

public class ExpiredCodeException extends RuntimeException {

    public ExpiredCodeException(String message) {
        super(message);
    }
}
