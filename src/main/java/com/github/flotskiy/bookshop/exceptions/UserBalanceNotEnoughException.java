package com.github.flotskiy.bookshop.exceptions;

public class UserBalanceNotEnoughException extends RuntimeException {

    public UserBalanceNotEnoughException(String message) {
        super(message);
    }
}
