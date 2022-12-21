package com.github.flotskiy.bookshop.repository;

import com.github.flotskiy.bookshop.model.entity.author.AuthorEntity;
import com.github.flotskiy.bookshop.model.entity.book.BookEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookRepositoryTests {

    private final BookRepository bookRepository;

    @Autowired
    public BookRepositoryTests(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Test
    void findBookEntitiesByAuthorEntitiesNameContaining() {
        String token = "Lana";
        List<BookEntity> bookEntities = bookRepository.findBookEntitiesByAuthorEntitiesNameContaining(token);
        assertNotNull(bookEntities);
        assertFalse(bookEntities.isEmpty());
        for (BookEntity bookEntity : bookEntities) {
            String allAuthors = bookEntity.getAuthorEntities()
                    .stream().map(AuthorEntity::getName).collect(Collectors.joining(","));
            Logger.getLogger(this.getClass().getSimpleName()).info(bookEntity.toString() + " and authors: " + allAuthors);
            assertThat(allAuthors.contains(token));
        }
    }

    @Test
    void findBookEntitiesByTitleContaining() {
        String token = "Fish";
        List<BookEntity> bookEntities = bookRepository.findBookEntitiesByTitleContaining(token);
        assertNotNull(bookEntities);
        assertFalse(bookEntities.isEmpty());
        for (BookEntity bookEntity : bookEntities) {
            Logger.getLogger(this.getClass().getSimpleName()).info(bookEntity.toString());
            assertThat(bookEntity.getTitle().contains(token));
        }
    }

    @Test
    void getBestsellers() {
        List<BookEntity> bookEntities = bookRepository.getBestsellers();
        assertNotNull(bookEntities);
        assertFalse(bookEntities.isEmpty());
        assertThat(bookEntities.size()).isGreaterThan(1);
        for (BookEntity bookEntity : bookEntities) {
            Logger.getLogger(this.getClass().getSimpleName()).info(bookEntity.toString());
            assertThat(bookEntity.getIsBestseller());
        }
    }
}
