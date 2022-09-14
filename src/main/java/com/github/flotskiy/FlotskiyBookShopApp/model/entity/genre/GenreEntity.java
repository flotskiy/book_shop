package com.github.flotskiy.FlotskiyBookShopApp.model.entity.genre;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "genre")
public class GenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "parent_id", columnDefinition = "INT")
    private Integer parentId;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @ManyToMany(mappedBy="genreEntities")
    private Set<BookEntity> books;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    @Override
    public String toString() {
        return "GenreEntity{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", slug='" + slug + '\'' +
                ", name='" + name + '\'' +
                ", books=" + books +
                '}';
    }
}
