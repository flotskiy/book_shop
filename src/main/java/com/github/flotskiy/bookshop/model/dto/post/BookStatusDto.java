package com.github.flotskiy.bookshop.model.dto.post;

import java.util.List;

public class BookStatusDto {

    private List<Integer> booksIds;
    private String status;

    public List<Integer> getBooksIds() {
        return booksIds;
    }

    public void setBooksIds(List<Integer> booksIds) {
        this.booksIds = booksIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
