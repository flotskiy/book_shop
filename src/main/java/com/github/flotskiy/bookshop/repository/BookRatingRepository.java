package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.model.entity.book.review.BookRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRatingRepository extends JpaRepository<BookRatingEntity, Integer> {

    List<BookRatingEntity> findBookRatingEntitiesByBookId(Integer bookId);

    BookRatingEntity findBookRatingEntityByBookIdAndUserId(Integer bookId, Integer userId);

    @Query(value = "SELECT max(id) FROM rating", nativeQuery = true)
    Integer getMaxId();

    @Query(
            value = "SELECT r.book_id FROM rating r GROUP BY book_id HAVING (AVG(r.rating) >= 4) " +
                    "ORDER BY AVG(r.rating) DESC LIMIT 30",
            nativeQuery = true
    )
    List<Integer> getFirst30bookIdsWithMaxUsersRatingMoreOrEquals4();
}
