package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.model.entity.book.links.Book2UserTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Book2UserTypeRepository extends JpaRepository<Book2UserTypeEntity, Integer> {
}
