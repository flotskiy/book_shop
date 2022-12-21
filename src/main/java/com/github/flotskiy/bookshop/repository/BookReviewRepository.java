package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.model.entity.book.review.BookReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookReviewRepository extends JpaRepository<BookReviewEntity, Integer> {

    List<BookReviewEntity> findBookReviewEntitiesByBookId(Integer bookId);

    BookReviewEntity findBookReviewEntityByBookIdAndUserId(Integer bookId, Integer userId);

    @Query(value = "SELECT max(id) FROM book_review", nativeQuery = true)
    Integer getMaxId();
}
