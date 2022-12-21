package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.model.entity.book.file.BookFileTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookFileTypeRepository extends JpaRepository<BookFileTypeEntity, Integer> {
}
