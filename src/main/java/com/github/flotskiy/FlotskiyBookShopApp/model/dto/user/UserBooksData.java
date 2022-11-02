package com.github.flotskiy.FlotskiyBookShopApp.model.dto.user;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<BookDto> getAllBooks() {
        return Stream.of(kept, cart, paid, archived)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
