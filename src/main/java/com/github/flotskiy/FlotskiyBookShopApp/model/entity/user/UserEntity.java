package com.github.flotskiy.FlotskiyBookShopApp.model.entity.user;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.review.BookReviewLikeEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.payments.BalanceTransactionEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY) TODO: REMOVE COMMENT LATER
    private Integer id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String hash;

    @Column(name = "reg_time", columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime regTime;

    @Column(columnDefinition = "INT NOT NULL DEFAULT 0")
    private Integer balance;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @ManyToMany(mappedBy="userEntities")
    private Set<BookEntity> books;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "file_download",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "book_id")})
    private Set<BookEntity> booksDownloadCounters;

    @OneToMany(mappedBy = "userId")
    private Set<BalanceTransactionEntity> balanceTransactionEntities;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "book_review",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "book_id")})
    private Set<BookEntity> booksReviewed;

    @ManyToMany(mappedBy="ratedByUserEntities")
    private Set<BookEntity> booksRated;

    @OneToMany(mappedBy = "userEntityLiked")
    private Set<BookReviewLikeEntity> bookReviewLikeEntities;

    @OneToOne(mappedBy = "userEntity")
    private UserContactEntity userContactEntity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public LocalDateTime getRegTime() {
        return regTime;
    }

    public void setRegTime(LocalDateTime regTime) {
        this.regTime = regTime;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<BookEntity> getBooks() {
        return books;
    }

    public void setBooks(Set<BookEntity> books) {
        this.books = books;
    }

    public Set<BookEntity> getBooksDownloadCounters() {
        return booksDownloadCounters;
    }

    public void setBooksDownloadCounters(Set<BookEntity> booksDownloadCounters) {
        this.booksDownloadCounters = booksDownloadCounters;
    }

    public Set<BalanceTransactionEntity> getBalanceTransactionEntities() {
        return balanceTransactionEntities;
    }

    public void setBalanceTransactionEntities(Set<BalanceTransactionEntity> balanceTransactionEntities) {
        this.balanceTransactionEntities = balanceTransactionEntities;
    }

    public Set<BookEntity> getBooksReviewed() {
        return booksReviewed;
    }

    public void setBooksReviewed(Set<BookEntity> booksReviewed) {
        this.booksReviewed = booksReviewed;
    }

    public Set<BookEntity> getBooksRated() {
        return booksRated;
    }

    public void setBooksRated(Set<BookEntity> booksRated) {
        this.booksRated = booksRated;
    }

    public Set<BookReviewLikeEntity> getBookReviewLikeEntities() {
        return bookReviewLikeEntities;
    }

    public void setBookReviewLikeEntities(Set<BookReviewLikeEntity> bookReviewLikeEntities) {
        this.bookReviewLikeEntities = bookReviewLikeEntities;
    }

    public UserContactEntity getUserContactEntity() {
        return userContactEntity;
    }

    public void setUserContactEntity(UserContactEntity userContactEntity) {
        this.userContactEntity = userContactEntity;
    }
}
