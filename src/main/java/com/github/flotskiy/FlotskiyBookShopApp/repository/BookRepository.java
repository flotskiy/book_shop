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

    List<BookEntity> findBookEntitiesByPriceBetween(int min, int max);

    List<BookEntity> findBookEntitiesByPriceIs(int price);

    @Query("from BookEntity where isBestseller = 1")
    List<BookEntity> getBestsellers();

    @Query(value = "SELECT * FROM book WHERE discount = (SELECT MAX(discount) FROM book)", nativeQuery = true)
    List<BookEntity> getBookEntitiesWithMaxDiscount();

    Page<BookEntity> findBookEntitiesByTitleContaining(String bookTitle, Pageable nextPage);

    Page<BookEntity> findBookEntitiesByPubDateBetweenOrderByPubDateDesc(LocalDate from, LocalDate to, Pageable nextPage);

    @Query(value = "SELECT * FROM book b WHERE b.id IN :idList", nativeQuery = true)
    List<BookEntity> findBookEntitiesByIdIsIn(@Param("idList") Collection<Integer> idList);

    Page<BookEntity> findBookEntitiesByBookTagsId(Integer tagId, Pageable nextPage);
}
