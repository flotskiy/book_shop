package com.github.flotskiy.FlotskiyBookShopApp.exceptions;

public class ExpiredCodeException extends RuntimeException {

    public ExpiredCodeException(String message) {
        super(message);
    }
}
