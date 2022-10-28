package com.github.flotskiy.FlotskiyBookShopApp.repository;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {

    List<Book2UserEntity> findBook2UserEntitiesByUserIdAndTypeId(Integer userId, Integer typeId);
}
