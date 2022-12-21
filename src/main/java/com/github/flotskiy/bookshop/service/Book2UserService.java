package com.github.flotskiy.bookshop.service;

import com.github.flotskiy.bookshop.model.entity.book.links.Book2UserEntity;
import com.github.flotskiy.bookshop.model.entity.book.links.Book2UserTypeEntity;
import com.github.flotskiy.bookshop.repository.Book2UserRepository;
import com.github.flotskiy.bookshop.repository.Book2UserTypeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class Book2UserService {

    private static final String FALSE_RESULT = "false";

    private Map<String, Integer> bookToUserTypeMap;
    private final Book2UserRepository book2UserRepository;
    private final Book2UserTypeRepository book2UserTypeRepository;

    public Book2UserService(Book2UserRepository book2UserRepository, Book2UserTypeRepository book2UserTypeRepository) {
        this.book2UserRepository = book2UserRepository;
        this.book2UserTypeRepository = book2UserTypeRepository;
    }

    public Map<String, Integer> getBookToUserTypeMap() {
        if (bookToUserTypeMap == null) {
            fillInBookToUserTypeMap();
        }
        return bookToUserTypeMap;
    }

    public String getBookStatus(Integer bookId, Integer userId) {
        if (userId < 1) {
            return FALSE_RESULT;
        }
        Book2UserEntity book2UserEntity = book2UserRepository.findBook2UserEntityByBookIdAndUserId(bookId, userId);
        if (book2UserEntity == null) {
            return FALSE_RESULT;
        }
        Integer statusId = book2UserEntity.getTypeId();
        return findBookStatusStringById(statusId);
    }

    public String findBookStatusStringById(Integer statusId) {
        for (Map.Entry<String, Integer> entry : getBookToUserTypeMap().entrySet()) {
            if (entry.getValue().equals(statusId)) {
                return entry.getKey();
            }
        }
        return FALSE_RESULT;
    }

    public Book2UserEntity findBook2UserEntityByBookIdAndUserId(Integer bookId, Integer userId) {
        return book2UserRepository.findBook2UserEntityByBookIdAndUserId(bookId, userId);
    }

    public void fillInBookToUserTypeMap() {
        List<Book2UserTypeEntity> book2UserTypeEntities = book2UserTypeRepository.findAll();
        bookToUserTypeMap = book2UserTypeEntities.stream()
                .collect(Collectors.toMap(Book2UserTypeEntity::getName, Book2UserTypeEntity::getId));
        Logger.getLogger(this.getClass().getSimpleName())
                .log(Level.INFO, "get info from DB about Book2User types: {0}", bookToUserTypeMap);
    }

    public Book2UserEntity saveBook2UserEntry(String status, Integer bookId, Integer userId) {
        Book2UserEntity book2UserEntity = book2UserRepository.findBook2UserEntityByBookIdAndUserId(bookId, userId);
        if (book2UserEntity == null) {
            book2UserEntity = new Book2UserEntity();
            Integer currentMaxId = book2UserRepository.getMaxId();
            int newBook2UserId = 1;
            if (currentMaxId != null) {
                newBook2UserId =  currentMaxId + 1;
            }
            book2UserEntity.setId(newBook2UserId);
            book2UserEntity.setBookId(bookId);
            book2UserEntity.setUserId(userId);
        }
        int typeId = getBookToUserTypeMap().get(status);
        book2UserEntity.setTypeId(typeId);
        book2UserEntity.setTime(LocalDateTime.now());
        return book2UserRepository.save(book2UserEntity);
    }

    public void removeBook2UserEntry(Integer bookId, Integer userId) {
        book2UserRepository.deleteBook2UserEntityByBookIdAndUserId(bookId, userId);
    }
}
