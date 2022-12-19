package com.github.flotskiy.FlotskiyBookShopApp.exceptions;

public class WrongBookStatusException extends RuntimeException {

    public WrongBookStatusException(String message) {
        super(message);
    }
}
