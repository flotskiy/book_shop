package com.github.flotskiy.FlotskiyBookShopApp.model.dto;

public class HeaderInfoDto {

    private String searchQuery;
    private Integer keptBooksCount = 0;
    private Integer cartBooksCount = 0;

    public HeaderInfoDto(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public HeaderInfoDto() {
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Integer getKeptBooksCount() {
        return keptBooksCount;
    }

    public void setKeptBooksCount(Integer keptBooksCount) {
        this.keptBooksCount = keptBooksCount;
    }

    public Integer getCartBooksCount() {
        return cartBooksCount;
    }

    public void setCartBooksCount(Integer cartBooksCount) {
        this.cartBooksCount = cartBooksCount;
    }
}
