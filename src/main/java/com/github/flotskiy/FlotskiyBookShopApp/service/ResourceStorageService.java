package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.file.BookFileEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.file.BookFileTypeEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.file.FileDownloadEntity;
import com.github.flotskiy.FlotskiyBookShopApp.repository.BookFileRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.BookFileTypeRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.FileDownloadRepository;
import com.github.flotskiy.FlotskiyBookShopApp.util.CustomStringHandler;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@PropertySource("application-variables.properties")
public class ResourceStorageService {

    @Value("${upload.path}")
    String uploadPath;

    @Value("${download.path}")
    String downloadPath;

    @Value("${book.status.paid}")
    private String paid;

    @Value("${download.limit}")
    private int downloadLimit;

    private final BookFileRepository bookFileRepository;
    private final FileDownloadRepository fileDownloadRepository;
    private final BookFileTypeRepository bookFileTypeRepository;
    private final Book2UserService book2UserService;
    private final CustomStringHandler customStringHandler;

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private Map<Integer, BookFileTypeEntity> bookFileTypeEntityMap;
    @Autowired
    public ResourceStorageService(
            BookFileRepository bookFileRepository,
            FileDownloadRepository fileDownloadRepository,
            BookFileTypeRepository bookFileTypeRepository,
            Book2UserService book2UserService,
            CustomStringHandler customStringHandler
    ) {
        this.bookFileRepository = bookFileRepository;
        this.fileDownloadRepository = fileDownloadRepository;
        this.bookFileTypeRepository = bookFileTypeRepository;
        this.book2UserService = book2UserService;
        this.customStringHandler = customStringHandler;
    }

    public Map<Integer, BookFileTypeEntity> getBookFileTypeEntityMap() {
        if (bookFileTypeEntityMap == null) {
            fillInBookFileTypeEntityMap();
        }
        return bookFileTypeEntityMap;
    }

    public String saveNewBookImage(MultipartFile file, String slug) throws IOException {
        String resourceURI = null;
        if (!file.isEmpty()) {
            if (!new File(uploadPath).exists()) {
                Files.createDirectories(Paths.get(uploadPath));
                Logger.getLogger(this.getClass().getSimpleName()).info("created image folder: " + uploadPath);
            }

            String fileName = slug + "." + FilenameUtils.getExtension(file.getOriginalFilename());
            Path path = Paths.get(uploadPath, fileName);
            resourceURI = "/book-covers/" + fileName;
            file.transferTo(path);
            Logger.getLogger(this.getClass().getSimpleName()).info(fileName + " uploaded SUCCESSFULLY!");
        }
        return resourceURI;
    }

    public Path getBookFilePath(String hash) {
        BookFileEntity bookFileEntity = bookFileRepository.findBookFileEntityByHash(hash);
        return Paths.get(bookFileEntity.getPath());
    }

    public MediaType getBookFileMime(String hash) {
        BookFileEntity bookFileEntity = bookFileRepository.findBookFileEntityByHash(hash);
        String mimeType =
                URLConnection.guessContentTypeFromName(Paths.get(bookFileEntity.getPath()).getFileName().toString());
        if (mimeType != null) {
            return MediaType.parseMediaType(mimeType);
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    public byte[] getBookFileByteArray(String hash) throws IOException {
        BookFileEntity bookFileEntity = bookFileRepository.findBookFileEntityByHash(hash);
        Path path = Paths.get(downloadPath, bookFileEntity.getPath());
        return Files.readAllBytes(path);
    }

    public Integer getBookId(String hash) {
        return bookFileRepository.findBookFileEntityByHash(hash).getBookEntity().getId();
    }

    public boolean isStatusPaid(Integer bookId, Integer userId) {
        return book2UserService.getBookStatus(bookId, userId).equals(paid);
    }

    @Transactional
    public void recordDownload(Integer bookId, Integer userId) {
        FileDownloadEntity fileDownloadEntity =
                fileDownloadRepository.findFileDownloadEntityByBookIdAndUserId(bookId, userId);
        if (fileDownloadEntity == null) {
            fileDownloadEntity = new FileDownloadEntity();
            fileDownloadEntity.setBookId(bookId);
            fileDownloadEntity.setUserId(userId);
        }
        fileDownloadEntity.setCount(fileDownloadEntity.getCount() + 1);
        fileDownloadRepository.save(fileDownloadEntity);
    }

    public boolean isDownloadAllowed(Integer bookId, Integer userId) {
        FileDownloadEntity fileDownloadEntity =
                fileDownloadRepository.findFileDownloadEntityByBookIdAndUserId(bookId, userId);
        if (fileDownloadEntity != null) {
            return fileDownloadEntity.getCount() < downloadLimit;
        }
        return true;
    }

    public String getFileSize(String hash) {
        byte[] file = new byte[0];
        try {
            file = getBookFileByteArray(hash);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return customStringHandler.getFileSizeString(file.length);
    }

    private void fillInBookFileTypeEntityMap() {
        List<BookFileTypeEntity> bookFileTypeEntityList = bookFileTypeRepository.findAll();
        bookFileTypeEntityMap = bookFileTypeEntityList.stream()
                .collect(Collectors.toMap(BookFileTypeEntity::getId, bookFileTypeEntity -> bookFileTypeEntity));
        logger.info("get info from DB about BookFileType entities. The are total entities number: " +
                        bookFileTypeEntityMap.size());
    }
}
