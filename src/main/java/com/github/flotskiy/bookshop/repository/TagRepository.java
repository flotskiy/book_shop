package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.model.entity.book.BookTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<BookTagEntity, Integer> {

    BookTagEntity findTitleBySlug(String slug);
}
