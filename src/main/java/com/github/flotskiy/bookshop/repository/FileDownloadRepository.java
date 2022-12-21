package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.model.entity.book.file.FileDownloadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDownloadRepository extends JpaRepository<FileDownloadEntity, Integer> {

    FileDownloadEntity findFileDownloadEntityByBookIdAndUserId(Integer bookId, Integer userId);
}
