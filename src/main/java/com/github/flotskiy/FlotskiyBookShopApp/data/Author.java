package com.github.flotskiy.FlotskiyBookShopApp.data;

public class Author {

    private Integer id;
    private String author;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", name='" + author + '\'' +
                '}';
    }
}
