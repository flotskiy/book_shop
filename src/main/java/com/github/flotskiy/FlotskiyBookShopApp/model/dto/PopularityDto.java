package com.github.flotskiy.FlotskiyBookShopApp.model.dto;

public class PopularityDto {

    private Integer bookId;
    private Short popularity;

    public PopularityDto() {}

    public PopularityDto(Integer bookId, Short popularity) {
        this.bookId = bookId;
        this.popularity = popularity;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Short getPopularity() {
        return popularity;
    }

    public void setPopularity(Short popularity) {
        this.popularity = popularity;
    }

    @Override
    public String toString() {
        return "PopularityDto{" +
                "bookId=" + bookId +
                ", popularity=" + popularity +
                '}';
    }
}
