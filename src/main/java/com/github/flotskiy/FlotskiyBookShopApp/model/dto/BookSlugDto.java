package com.github.flotskiy.FlotskiyBookShopApp.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Set;

public class BookSlugDto {

    private int id;
    private String slug;
    private String image;
    private Set<AuthorDto> authors;
    private String title;
    private short discount;
    @JsonProperty("isBestseller")
    private boolean isBestseller;
    private short rating;
    private String status;
    private int price;
    private int discountPrice;
    private LocalDate pubDate;
    private String description;
    private Set<TagDto> tags;
    private Set<BookFileDto> bookFileDtos;

    public BookSlugDto(BookDto bookDto) {
        this.id = bookDto.getId();
        this.slug = bookDto.getSlug();
        this.image = bookDto.getImage();
        this.title = bookDto.getTitle();
        this.discount = bookDto.getDiscount();
        this.isBestseller = bookDto.isBestseller();
        this.rating = bookDto.getRating();
        this.status = bookDto.getStatus();
        this.price = bookDto.getPrice();
        this.discountPrice = bookDto.getDiscountPrice();
        this.pubDate = bookDto.getPubDate();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Set<AuthorDto> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<AuthorDto> authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public short getDiscount() {
        return discount;
    }

    public void setDiscount(short discount) {
        this.discount = discount;
    }

    public boolean isBestseller() {
        return isBestseller;
    }

    public void setBestseller(boolean bestseller) {
        isBestseller = bestseller;
    }

    public short getRating() {
        return rating;
    }

    public void setRating(short rating) {
        this.rating = rating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(int discountPrice) {
        this.discountPrice = discountPrice;
    }

    public LocalDate getPubDate() {
        return pubDate;
    }

    public void setPubDate(LocalDate pubDate) {
        this.pubDate = pubDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<TagDto> getTags() {
        return tags;
    }

    public void setTags(Set<TagDto> tags) {
        this.tags = tags;
    }

    public Set<BookFileDto> getBookFileDtos() {
        return bookFileDtos;
    }

    public void setBookFileDtos(Set<BookFileDto> bookFileDtos) {
        this.bookFileDtos = bookFileDtos;
    }
}
