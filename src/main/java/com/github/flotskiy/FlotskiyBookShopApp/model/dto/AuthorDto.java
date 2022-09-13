package com.github.flotskiy.FlotskiyBookShopApp.model.dto;

public class AuthorDto {

    private int id;
    private String name;
    private String photo;
    private String slug;
    private String descriptionShort;
    private String descriptionRemains;
    private int booksCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescriptionShort() {
        return descriptionShort;
    }

    public void setDescriptionShort(String descriptionShort) {
        this.descriptionShort = descriptionShort;
    }

    public String getDescriptionRemains() {
        return descriptionRemains;
    }

    public void setDescriptionRemains(String descriptionRemains) {
        this.descriptionRemains = descriptionRemains;
    }

    public int getBooksCount() {
        return booksCount;
    }

    public void setBooksCount(int booksCount) {
        this.booksCount = booksCount;
    }

    @Override
    public String toString() {
        return "AuthorDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", slug='" + slug + '\'' +
                ", descriptionShort='" + descriptionShort + '\'' +
                ", descriptionRemains='" + descriptionRemains + '\'' +
                ", booksCount=" + booksCount +
                '}';
    }
}
