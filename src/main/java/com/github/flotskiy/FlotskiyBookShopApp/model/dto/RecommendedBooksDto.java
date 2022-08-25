package com.github.flotskiy.FlotskiyBookShopApp.model.dto;

import java.util.List;

public class RecommendedBooksDto {

    private int count;
    private List<BookDto> books;

    public RecommendedBooksDto(List<BookDto> books) {
        this.books = books;
        this.count = books.size();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<BookDto> getBooks() {
        return books;
    }

    public void setBooks(List<BookDto> books) {
        this.books = books;
    }
}
