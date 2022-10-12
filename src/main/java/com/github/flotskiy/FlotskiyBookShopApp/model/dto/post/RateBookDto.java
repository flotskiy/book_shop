package com.github.flotskiy.FlotskiyBookShopApp.model.dto.post;

public class RateBookDto {

    private Integer bookId;
    private String value;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
