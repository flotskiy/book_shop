package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.model.entity.genre.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<GenreEntity, Integer> {

    GenreEntity findGenreEntityBySlug(String slug);
}
