package com.github.flotskiy.FlotskiyBookShopApp.repository;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface Book2AuthorRepository extends JpaRepository<Book2AuthorEntity, Integer> {

    @Query(value = "SELECT DISTINCT ba.author_id FROM book2author ba WHERE ba.book_id IN :bookIdList", nativeQuery = true)
    List<Integer> getAuthorIdsForBookIdsInList(@Param("bookIdList") Collection<Integer> bookIdList);

    @Query(value = "SELECT DISTINCT ba.book_id FROM book2author ba WHERE ba.author_id IN :authorIdList", nativeQuery = true)
    List<Integer> getBookIdsForAuthorIdsInList(@Param("authorIdList") Collection<Integer> authorIdList);

    List<Book2AuthorEntity> findBook2AuthorEntitiesByBookIdOrderBySortIndexAsc(Integer bookId);
}
