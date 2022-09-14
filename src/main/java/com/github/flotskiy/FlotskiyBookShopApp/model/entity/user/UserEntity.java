package com.github.flotskiy.FlotskiyBookShopApp.model.entity.user;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.review.BookReviewEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String hash;

    @Column(name = "reg_time", columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime regTime;

    @Column(columnDefinition = "INT NOT NULL DEFAULT 0")
    private int balance;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @ManyToMany(mappedBy="userEntities")
    private Set<BookEntity> books;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "file_download",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "book_id")})
    private Set<BookEntity> booksDownloadCounters;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "balance_transaction",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "book_id")})
    private Set<BookEntity> booksTransactions;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "book_review",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "book_id")})
    private Set<BookEntity> booksReviewed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
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

    public Set<BookEntity> getBooksTransactions() {
        return booksTransactions;
    }

    public void setBooksTransactions(Set<BookEntity> booksTransactions) {
        this.booksTransactions = booksTransactions;
    }

    public Set<BookEntity> getBooksReviewed() {
        return booksReviewed;
    }

    public void setBooksReviewed(Set<BookEntity> booksReviewed) {
        this.booksReviewed = booksReviewed;
    }
}
