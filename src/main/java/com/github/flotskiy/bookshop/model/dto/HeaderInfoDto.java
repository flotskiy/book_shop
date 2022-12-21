package com.github.flotskiy.bookshop.model.dto;

public class HeaderInfoDto {

    private String searchQuery;
    private Integer keptBooksCount = 0;
    private Integer cartBooksCount = 0;
    private Integer myBooksCount = 0;
    private String userName = "";
    private Integer userBalance = 0;

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

    public Integer getMyBooksCount() {
        return myBooksCount;
    }

    public void setMyBooksCount(Integer myBooksCount) {
        this.myBooksCount = myBooksCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(Integer userBalance) {
        this.userBalance = userBalance;
    }
}
