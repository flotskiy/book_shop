package com.github.flotskiy.FlotskiyBookShopApp.model.dto;

import java.time.LocalDate;

public class BookDto {

    private int id;
    private String slug;
    private String image;
    private String authors;
    private String title;
    private short discount;
    private boolean isBestseller;
    private short rating;
    private String status;
    private int price;
    private int discountPrice;
    private LocalDate pubDate;

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

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
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

    @Override
    public String toString() {
        return "BookDto{" +
                "id=" + id +
                ", slug='" + slug + '\'' +
                ", image='" + image + '\'' +
                ", authors='" + authors + '\'' +
                ", title='" + title + '\'' +
                ", discount=" + discount +
                ", isBestseller=" + isBestseller +
                ", rating=" + rating +
                ", status='" + status + '\'' +
                ", price=" + price +
                ", discountPrice=" + discountPrice +
                ", pubDate=" + pubDate +
                '}';
    }
}
