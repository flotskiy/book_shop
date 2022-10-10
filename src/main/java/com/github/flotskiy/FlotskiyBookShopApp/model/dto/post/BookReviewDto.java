package com.github.flotskiy.FlotskiyBookShopApp.model.dto.post;

public class BookReviewDto {

    private Integer bookId;
    private String text;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
