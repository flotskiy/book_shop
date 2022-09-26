package com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.review;

import javax.persistence.*;

@Entity
@Table(name = "rating")
public class BookRatingEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY) TODO: REMOVE COMMENT LATER
    private Integer id;

    @Column(name = "book_id", columnDefinition = "INT NOT NULL")
    private Integer bookId;

    @Column(name = "user_id", columnDefinition = "INT NOT NULL")
    private Integer userId;

    @Column(columnDefinition = "SMALLINT NOT NULL")
    private Short rating;

    public BookRatingEntity() {}

    public BookRatingEntity(Integer id, Integer bookId, Integer userId, Short rating) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.rating = rating;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Short getRating() {
        return rating;
    }

    public void setRating(Short rating) {
        this.rating = rating;
    }
}
