package com.github.flotskiy.FlotskiyBookShopApp.repository;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Book2GenreRepository extends JpaRepository<Book2GenreEntity, Integer> {
}
