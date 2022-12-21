package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.security.jwt.InactiveJwt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface InactiveJwtRepository extends JpaRepository<InactiveJwt, Integer> {

    void deleteInactiveJwtsByExpiryBefore(LocalDateTime localDateTime);

    InactiveJwt findInactiveJwtByHash(String hash);
}
