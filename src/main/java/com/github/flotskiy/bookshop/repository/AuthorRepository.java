package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.model.entity.author.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<AuthorEntity, Integer> {

    AuthorEntity findAuthorEntityBySlug(String authorSlug);
}
