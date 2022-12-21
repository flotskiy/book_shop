package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.model.entity.user.VerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationTokenEntity, Integer> {

    VerificationTokenEntity findVerificationTokenEntityByToken(String token);
}
