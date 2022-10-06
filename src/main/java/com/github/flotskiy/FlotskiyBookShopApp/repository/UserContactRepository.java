package com.github.flotskiy.FlotskiyBookShopApp.repository;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserContactRepository extends JpaRepository<UserContactEntity, Integer> {

    @Query(value = "SELECT max(id) FROM user_contact", nativeQuery = true)
    Integer getMaxId();

    UserContactEntity findUserContactEntityByUserEntityId(Integer userId);
}
