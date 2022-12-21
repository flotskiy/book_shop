package com.github.flotskiy.bookshop.model.entity.book.links;

import javax.persistence.*;

@Entity
@Table(name = "tag2book")
public class BookTag2BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "tag_id", columnDefinition = "INT NOT NULL")
    private int tagId;

    @Column(name = "book_id", columnDefinition = "INT NOT NULL")
    private int bookId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
}
