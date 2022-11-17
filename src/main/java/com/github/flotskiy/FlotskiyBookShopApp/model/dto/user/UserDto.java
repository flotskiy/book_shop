package com.github.flotskiy.FlotskiyBookShopApp.model.dto.user;

public class UserDto {

    private Integer id;
    private String name;
    private String password;
    private Integer balance;
    private String contact;
    private UserBooksData userBooksData;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public UserBooksData getUserBooksData() {
        return userBooksData;
    }

    public void setUserBooksData(UserBooksData userBooksData) {
        this.userBooksData = userBooksData;
    }
}
