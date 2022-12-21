package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.model.entity.book.file.BookFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookFileRepository extends JpaRepository<BookFileEntity, Integer> {

    BookFileEntity findBookFileEntityByHash(String hash);
}
