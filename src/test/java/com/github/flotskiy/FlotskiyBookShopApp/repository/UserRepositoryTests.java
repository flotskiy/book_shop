package com.github.flotskiy.FlotskiyBookShopApp.repository;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class UserRepositoryTests {

    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTests(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    public void testAddNewUser() {
        UserEntity newUserEntity = new UserEntity();
        int newUserId = userRepository.getMaxId() + 1;
        newUserEntity.setId(newUserId);
        newUserEntity.setName("New Test User Lalalalalala");
        newUserEntity.setBalance(10000);
        newUserEntity.setRegTime(LocalDateTime.now());
        newUserEntity.setHash("skdghbcew7fdsf-1");

        assertNotNull(userRepository.save(newUserEntity));
    }
}
