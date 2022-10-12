package com.github.flotskiy.FlotskiyBookShopApp.repository;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.enums.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findUserEntityByUserContactEntity_TypeAndUserContactEntity_Contact(ContactType contactType, String contact);

    @Query(value = "SELECT max(id) FROM users", nativeQuery = true)
    Integer getMaxId();
}
