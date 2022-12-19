package com.github.flotskiy.FlotskiyBookShopApp.repository;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.file.BookFileTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookFileTypeRepository extends JpaRepository<BookFileTypeEntity, Integer> {
}
