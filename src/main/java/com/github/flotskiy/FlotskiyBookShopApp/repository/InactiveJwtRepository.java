package com.github.flotskiy.FlotskiyBookShopApp.repository;

import com.github.flotskiy.FlotskiyBookShopApp.security.jwt.InactiveJwt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface InactiveJwtRepository extends JpaRepository<InactiveJwt, Integer> {

    void deleteInactiveJwtsByExpiryBefore(LocalDateTime localDateTime);

    InactiveJwt findInactiveJwtByHash(String hash);
}
