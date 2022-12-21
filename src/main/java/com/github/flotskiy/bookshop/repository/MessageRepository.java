package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.model.entity.book.review.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {
}
