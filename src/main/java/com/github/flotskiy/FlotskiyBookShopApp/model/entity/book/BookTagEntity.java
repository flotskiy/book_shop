package com.github.flotskiy.FlotskiyBookShopApp.model.entity.book;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tag")
public class BookTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255)")
    private String title;

    @Column(columnDefinition = "VARCHAR(255)")
    private String slug;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "tag2book",
            joinColumns = {@JoinColumn(name = "tag_id")},
            inverseJoinColumns = {@JoinColumn(name = "book_id")})
    private Set<BookEntity> bookEntities;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Set<BookEntity> getBookEntities() {
        return bookEntities;
    }

    public void setBookEntities(Set<BookEntity> bookEntities) {
        this.bookEntities = bookEntities;
    }

    @Override
    public String toString() {
        return "BookTagEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
