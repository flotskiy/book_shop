package com.github.flotskiy.FlotskiyBookShopApp.repository;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.review.BookRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRatingRepository extends JpaRepository<BookRatingEntity, Integer> {

    List<BookRatingEntity> findBookRatingEntitiesByBookId(Integer bookId);

    BookRatingEntity findBookRatingEntityByBookIdAndUserId(Integer bookId, Integer userId);

    @Query(value = "SELECT max(id) FROM rating", nativeQuery = true)
    Integer getMaxId();

    @Query(
            value = "SELECT r.book_id FROM rating r GROUP BY book_id ORDER BY AVG(r.rating) DESC LIMIT 30",
            nativeQuery = true
    )
    List<Integer> getFirst30bookIdsWithMaxUsersRating();
}
