package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.model.entity.book.review.BookReviewLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookReviewLikeRepository extends JpaRepository<BookReviewLikeEntity, Integer> {

    List<BookReviewLikeEntity> findBookReviewLikeEntitiesByReviewId(Integer reviewId);

    @Query(
            value = "SELECT * FROM book_review_like l WHERE l.review_id = :reviewId AND l.user_id = :userId",
            nativeQuery = true
    )
    BookReviewLikeEntity findBookReviewLikeEntityByReviewIdAndUserId(
            @Param("reviewId") Integer reviewId, @Param("userId") Integer userId
    );

    @Query(value = "SELECT max(id) FROM book_review_like", nativeQuery = true)
    Integer getMaxId();
}
