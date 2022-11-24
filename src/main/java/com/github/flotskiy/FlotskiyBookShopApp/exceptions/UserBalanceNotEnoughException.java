package com.github.flotskiy.FlotskiyBookShopApp.exceptions;

public class UserBalanceNotEnoughException extends RuntimeException {

    public UserBalanceNotEnoughException(String message) {
        super(message);
    }
}
