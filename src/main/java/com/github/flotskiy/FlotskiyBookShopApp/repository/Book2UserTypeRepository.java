package com.github.flotskiy.FlotskiyBookShopApp.repository;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2UserTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Book2UserTypeRepository extends JpaRepository<Book2UserTypeEntity, Integer> {
}
