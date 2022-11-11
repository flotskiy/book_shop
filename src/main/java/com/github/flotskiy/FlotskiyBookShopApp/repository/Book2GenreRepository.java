package com.github.flotskiy.FlotskiyBookShopApp.repository;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface Book2GenreRepository extends JpaRepository<Book2GenreEntity, Integer> {

    @Query(value = "SELECT DISTINCT bg.genre_id FROM book2genre bg WHERE bg.book_id IN :bookIdList", nativeQuery = true)
    List<Integer> getGenreIdsForBooksIdsInList(@Param("bookIdList") Collection<Integer> bookIdList);

    @Query(value = "SELECT DISTINCT bg.book_id FROM book2genre bg WHERE bg.genre_id IN :genreIdList", nativeQuery = true)
    List<Integer> getBookIdsForGenreIdsInList(@Param("genreIdList") Collection<Integer> genreIdList);
}
