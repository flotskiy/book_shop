package com.github.flotskiy.FlotskiyBookShopApp.repository;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.BookTag2BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface BookTag2BookRepository extends JpaRepository<BookTag2BookEntity, Integer> {

    @Query(value = "SELECT DISTINCT tb.tag_id FROM tag2book tb WHERE tb.book_id IN :bookIdList", nativeQuery = true)
    List<Integer> getTagIdsForBooksIdsInList(@Param("bookIdList") Collection<Integer> bookIdList);

    @Query(value = "SELECT DISTINCT tb.book_id FROM tag2book tb WHERE tb.tag_id IN :tagIdList", nativeQuery = true)
    List<Integer> getBookIdsForTagIdsInList(@Param("tagIdList") Collection<Integer> tagIdList);
}
