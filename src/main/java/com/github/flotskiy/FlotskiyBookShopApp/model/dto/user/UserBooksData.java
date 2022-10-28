package com.github.flotskiy.FlotskiyBookShopApp.model.dto.user;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;

import java.util.List;

public class UserBooksData {

    private List<BookDto> kept;
    private List<BookDto> cart;
    private List<BookDto> paid;
    private List<BookDto> archived;

    public List<BookDto> getKept() {
        return kept;
    }

    public void setKept(List<BookDto> kept) {
        this.kept = kept;
    }

    public List<BookDto> getCart() {
        return cart;
    }

    public void setCart(List<BookDto> cart) {
        this.cart = cart;
    }

    public List<BookDto> getPaid() {
        return paid;
    }

    public void setPaid(List<BookDto> paid) {
        this.paid = paid;
    }

    public List<BookDto> getArchived() {
        return archived;
    }

    public void setArchived(List<BookDto> archived) {
        this.archived = archived;
    }
}
