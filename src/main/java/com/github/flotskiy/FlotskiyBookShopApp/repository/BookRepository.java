package com.github.flotskiy.FlotskiyBookShopApp.repository;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    List<BookEntity> findBookEntitiesByAuthorEntitiesNameContaining(String name);

    List<BookEntity> findBookEntitiesByTitleContaining(String bookTitle);

    List<BookEntity> findBookEntitiesByPriceBetween(Integer min, Integer max);

    @Query("from BookEntity where isBestseller = 1")
    List<BookEntity> getBestsellers();

    @Query(value = "SELECT * FROM book WHERE discount = (SELECT MAX(discount) FROM book)", nativeQuery = true)
    List<BookEntity> getBookEntitiesWithMaxDiscount();

    Page<BookEntity> findBookEntitiesByTitleContaining(String bookTitle, Pageable nextPage);

    Integer countBookEntitiesByTitleContaining(String bookTitle);

    @Query(value =
            "SELECT * FROM book b WHERE b.pub_date BETWEEN :from AND :to AND b.id NOT IN :idList ORDER BY b.pub_date DESC, b.id DESC",
            nativeQuery = true)
    Page<BookEntity> findBookEntitiesByPubDateBetweenAndIdNotContainingOrderByPubDateDescIdDesc(
            @Param("from") LocalDate from, @Param("to") LocalDate to, @Param("idList") Collection<Integer> idList, Pageable nextPage
    );

    @Query(value = "SELECT * FROM book b WHERE b.id IN :idList", nativeQuery = true)
    List<BookEntity> findBookEntitiesByIdIsIn(@Param("idList") Collection<Integer> idList);

    @Query(value = "SELECT * FROM book b WHERE b.id IN :idList ORDER BY b.pub_date DESC", nativeQuery = true)
    Page<BookEntity> findBookEntitiesByIdIsInOrderByPubDageDesc(@Param("idList") Collection<Integer> idList, Pageable nextPage);

    Page<BookEntity> findBookEntitiesByBookTagsIdOrderByPubDateDesc(Integer tagId, Pageable nextPage);

    Page<BookEntity> findBookEntitiesByGenreEntitiesIdOrderByPubDateDesc(Integer genreId, Pageable nextPage);

    Page<BookEntity> findBookEntitiesByAuthorEntitiesIdOrderByPubDateDesc(Integer authorId, Pageable nextPage);

    BookEntity findBookEntityBySlug(String slug);

    List<BookEntity> findBookEntitiesBySlugIn(Collection<String> slug);
}
