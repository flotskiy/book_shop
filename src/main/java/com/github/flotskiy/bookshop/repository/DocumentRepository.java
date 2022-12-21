package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.model.entity.other.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Integer> {

    @Query(value = "SELECT * FROM document d WHERE d.id % 2 = 1 ORDER BY d.sort_index ASC", nativeQuery = true)
    List<DocumentEntity> getAllRuDocumentsOrderBySortIndexAsc();

    @Query(value = "SELECT * FROM document d WHERE d.id % 2 = 0 ORDER BY d.sort_index ASC", nativeQuery = true)
    List<DocumentEntity> getAllEngDocumentsOrderBySortIndexAsc();

    DocumentEntity findDocumentEntityBySlug(String slug);
}
