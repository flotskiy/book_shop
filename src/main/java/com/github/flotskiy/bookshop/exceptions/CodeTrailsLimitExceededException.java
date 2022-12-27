package com.github.flotskiy.bookshop.exceptions;

public class CodeTrailsLimitExceededException extends RuntimeException {

    public CodeTrailsLimitExceededException(String message) {
        super(message);
    }
}
