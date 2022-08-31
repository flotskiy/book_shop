package com.github.flotskiy.FlotskiyBookShopApp.model.dto;

import java.time.LocalDate;

public class BookDto {

    private int id;
    private LocalDate pubDate;
    private short isBestseller;
    private String author;
    private String title;
    private String image;
    private int priceOld;
    private int price;
    private short discount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getPubDate() {
        return pubDate;
    }

    public void setPubDate(LocalDate pubDate) {
        this.pubDate = pubDate;
    }

    public short getIsBestseller() {
        return isBestseller;
    }

    public void setIsBestseller(short isBestseller) {
        this.isBestseller = isBestseller;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPriceOld() {
        return priceOld;
    }

    public void setPriceOld(int priceOld) {
        this.priceOld = priceOld;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public short getDiscount() {
        return discount;
    }

    public void setDiscount(short discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "BookDto{" +
                "id=" + id +
                ", pubDate=" + pubDate +
                ", isBestseller=" + isBestseller +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", priceOld=" + priceOld +
                ", price=" + price +
                ", discount=" + discount +
                '}';
    }
}
