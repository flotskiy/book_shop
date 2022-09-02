package com.github.flotskiy.FlotskiyBookShopApp.model.dto;

public class RatingDto {

    private Integer bookId;
    private Short rating;

    public RatingDto() {}

    public RatingDto(Integer bookId, Short rating) {
        this.bookId = bookId;
        this.rating = rating;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Short getRating() {
        return rating;
    }

    public void setRating(Short rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "RatingDto{" +
                "bookId=" + bookId +
                ", rating=" + rating +
                '}';
    }
}
