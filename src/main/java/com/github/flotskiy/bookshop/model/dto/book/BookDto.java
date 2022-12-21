package com.github.flotskiy.bookshop.model.dto.book;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Objects;

public class BookDto {

    private Integer id;
    private String slug;
    private String image;
    private String authors;
    private String title;
    private Short discount;
    private boolean isBestseller;
    private Short rating;
    private String status;
    private Integer price;
    private Integer discountPrice;
    private LocalDate pubDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Short getDiscount() {
        return discount;
    }

    public void setDiscount(Short discount) {
        this.discount = discount;
    }

    @JsonProperty("isBestseller")
    public boolean isBestseller() {
        return isBestseller;
    }

    public void setIsBestseller(boolean isBestseller) {
        this.isBestseller = isBestseller;
    }

    public Short getRating() {
        return rating;
    }

    public void setRating(Short rating) {
        this.rating = rating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Integer discountPrice) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookDto bookDto = (BookDto) o;
        return isBestseller == bookDto.isBestseller
                && id.equals(bookDto.id)
                && authors.equals(bookDto.authors)
                && title.equals(bookDto.title)
                && rating.equals(bookDto.rating)
                && pubDate.equals(bookDto.pubDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id, authors, title, isBestseller, rating, pubDate
        );
    }
}
