package com.github.flotskiy.FlotskiyBookShopApp.repository;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {

    List<Book2UserEntity> findBook2UserEntitiesByUserIdAndTypeId(Integer userId, Integer typeId);

    Book2UserEntity findBook2UserEntityByBookIdAndUserId(Integer bookId, Integer userId);

    void deleteBook2UserEntityByBookIdAndUserId(Integer bookId, Integer userId);

    @Query(value = "SELECT max(id) FROM book2user", nativeQuery = true)
    Integer getMaxId();
}
