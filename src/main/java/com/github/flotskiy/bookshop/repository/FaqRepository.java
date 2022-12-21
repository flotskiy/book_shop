package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.model.entity.other.FaqEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FaqRepository extends JpaRepository<FaqEntity, Integer> {

    @Query(value = "SELECT * FROM faq f WHERE f.id % 2 = 1 ORDER BY f.sort_index ASC", nativeQuery = true)
    List<FaqEntity> getAllRuFaqOrderBySortIndexAsc();

    @Query(value = "SELECT * FROM faq f WHERE f.id % 2 = 0 ORDER BY f.sort_index ASC", nativeQuery = true)
    List<FaqEntity> getAllEngFaqOrderBySortIndexAsc();
}
