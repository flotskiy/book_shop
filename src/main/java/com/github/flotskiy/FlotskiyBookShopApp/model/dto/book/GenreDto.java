package com.github.flotskiy.FlotskiyBookShopApp.model.dto.book;

import java.util.Collections;
import java.util.List;

public class GenreDto {

    private Integer id;
    private Integer parentId;
    private String name;
    private String slug;
    private Integer booksCount;
    private List<GenreDto> children;
    private Boolean isTwiceInherited;

    public GenreDto() {
        this.children = Collections.emptyList();
        this.isTwiceInherited = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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

    public Boolean getTwiceInherited() {
        return isTwiceInherited;
    }

    public void setTwiceInherited(Boolean twiceInherited) {
        isTwiceInherited = twiceInherited;
    }
}
