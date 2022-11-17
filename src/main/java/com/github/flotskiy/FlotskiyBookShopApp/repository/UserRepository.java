package com.github.flotskiy.FlotskiyBookShopApp.repository;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findUserEntityByUserContactEntity_Contact(String contact);

    @Query(value = "SELECT max(id) FROM users", nativeQuery = true)
    Integer getMaxId();
}
