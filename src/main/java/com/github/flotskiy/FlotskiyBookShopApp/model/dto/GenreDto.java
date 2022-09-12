package com.github.flotskiy.FlotskiyBookShopApp.model.dto;

import java.util.Collections;
import java.util.List;

public class GenreDto {

    private Integer id;
    private String name;
    private String slug;
    private Integer booksCount;
    private List<GenreDto> children = Collections.emptyList();

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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getBooksCount() {
        return booksCount;
    }

    public void setBooksCount(Integer booksCount) {
        this.booksCount = booksCount;
    }

    public List<GenreDto> getChildren() {
        return children;
    }

    public void setChildren(List<GenreDto> children) {
        this.children = children;
    }
}
