package com.github.flotskiy.FlotskiyBookShopApp.repository;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {}
