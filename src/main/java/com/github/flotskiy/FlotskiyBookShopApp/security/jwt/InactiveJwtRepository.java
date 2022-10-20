package com.github.flotskiy.FlotskiyBookShopApp.security.jwt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface InactiveJwtRepository extends JpaRepository<InactiveJwt, Integer> {

    void removeInactiveJwtsByExpiryBefore(LocalDateTime localDateTime);

    InactiveJwt findInactiveJwtByHash(String hash);
}
