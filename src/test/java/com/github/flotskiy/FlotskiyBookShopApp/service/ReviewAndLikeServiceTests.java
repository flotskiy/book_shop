package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.exceptions.BookReviewException;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.page.BookReviewDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.review.BookReviewEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.review.BookReviewLikeEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.repository.BookRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.BookReviewLikeRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.BookReviewRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReviewAndLikeServiceTests {

    @InjectMocks
    private final ReviewAndLikeService reviewAndLikeService;

    @MockBean
    private final UserRepository userRepositoryMock;

    @MockBean
    private final BookRepository bookRepositoryMock;

    @MockBean
    private final BookReviewRepository bookReviewRepositoryMock;

    @MockBean
    private final BookReviewLikeRepository bookReviewLikeRepositoryMock;

    private final String reviewText = "This string is created for testing purpose";
    private final String testName = "Test Name";
    private BookEntity bookEntity;
    private Optional<BookEntity> bookEntityOptional;
    private BookReviewEntity bookReviewEntity;
    private Optional<BookReviewEntity> bookReviewEntityOptional;
    private UserEntity userEntity;
    private Optional<UserEntity> userEntityOptional;
    private BookReviewLikeEntity bookReviewLikeEntity1;
    private BookReviewLikeEntity bookReviewLikeEntity2;
    private BookReviewLikeEntity bookReviewLikeEntity3;
    private BookReviewLikeEntity bookReviewLikeEntity4;

    @Autowired
    public ReviewAndLikeServiceTests(
            ReviewAndLikeService reviewAndLikeService,
            UserRepository userRepositoryMock,
            BookRepository bookRepositoryMock,
            BookReviewRepository bookReviewRepositoryMock,
            BookReviewLikeRepository bookReviewLikeRepositoryMock
    ) {
        this.reviewAndLikeService = reviewAndLikeService;
        this.userRepositoryMock = userRepositoryMock;
        this.bookRepositoryMock = bookRepositoryMock;
        this.bookReviewRepositoryMock = bookReviewRepositoryMock;
        this.bookReviewLikeRepositoryMock = bookReviewLikeRepositoryMock;
    }

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setName(testName);
        userEntityOptional = Optional.of(userEntity);

        bookEntity = new BookEntity();
        bookEntity.setId(1);
        bookEntityOptional = Optional.of(bookEntity);

        bookReviewEntity = new BookReviewEntity();
        bookReviewEntity.setId(1);
        bookReviewEntity.setBookId(1);
        bookReviewEntity.setUserId(1);
        bookReviewEntity.setText(reviewText);
        bookReviewEntity.setTime(LocalDateTime.now());
        bookReviewEntityOptional = Optional.of(bookReviewEntity);

        bookReviewLikeEntity1 = new BookReviewLikeEntity();
        bookReviewLikeEntity1.setValue((short) 1);

        bookReviewLikeEntity2 = new BookReviewLikeEntity();
        bookReviewLikeEntity2.setValue((short) -1);

        bookReviewLikeEntity3 = new BookReviewLikeEntity();
        bookReviewLikeEntity3.setValue((short) 1);

        bookReviewLikeEntity4 = new BookReviewLikeEntity();
        bookReviewLikeEntity4.setValue((short) 1);
    }

    @AfterEach
    void tearDown() {
        bookEntity = null;
        bookReviewEntity = null;
        userEntity = null;
        bookReviewLikeEntity1 = null;
        bookReviewLikeEntity2 = null;
        bookReviewLikeEntity3 = null;
        bookReviewLikeEntity4 = null;
        userEntityOptional = Optional.empty();
        bookEntityOptional = Optional.empty();
        bookReviewEntityOptional = Optional.empty();
    }

    @Test
    void bookReview() throws BookReviewException {
        Mockito.doReturn(bookEntityOptional).when(bookRepositoryMock).findById(Mockito.any(Integer.class));
        Mockito.doReturn(bookReviewEntity).when(bookReviewRepositoryMock).save(Mockito.any(BookReviewEntity.class));

        BookReviewEntity bookReview = reviewAndLikeService.bookReview(1, 1, reviewText);

        assertNotNull(bookReview);
        assertEquals(1, bookReview.getId());
        assertEquals(1, bookReview.getUserId());
        assertEquals(1, bookReview.getBookId());
        assertEquals(reviewText, bookReview.getText());
        Mockito.verify(bookReviewRepositoryMock, Mockito.times(1))
                .findBookReviewEntityByBookIdAndUserId(Mockito.any(Integer.class), Mockito.any(Integer.class));
        Mockito.verify(bookReviewRepositoryMock, Mockito.times(1))
                .save(Mockito.any(BookReviewEntity.class));
    }

    @Test
    void convertBookReviewEntityToBookReviewDto() {
        Mockito.doReturn(userEntityOptional).when(userRepositoryMock).findById(Mockito.any(Integer.class));
        Mockito.doReturn(Arrays.asList(
                bookReviewLikeEntity1, bookReviewLikeEntity2, bookReviewLikeEntity3, bookReviewLikeEntity4
        )).when(bookReviewLikeRepositoryMock).findBookReviewLikeEntitiesByReviewId(Mockito.any(Integer.class));

        BookReviewDto bookReviewDto = reviewAndLikeService.convertBookReviewEntityToBookReviewDto(bookReviewEntity);
        assertNotNull(bookReviewDto);
        assertEquals(1, bookReviewDto.getId());
        assertEquals(testName, bookReviewDto.getUserName());
        assertEquals(reviewText, bookReviewDto.getText());
        assertEquals(3, bookReviewDto.getLikeCount());
        assertEquals(1, bookReviewDto.getDislikeCount());
        assertEquals(4, bookReviewDto.getRating());
        Mockito.verify(userRepositoryMock, Mockito.times(1))
                .findById(Mockito.any(Integer.class));
        Mockito.verify(bookReviewLikeRepositoryMock, Mockito.times(1))
                .findBookReviewLikeEntitiesByReviewId(Mockito.any(Integer.class));
    }

    @Test
    void calculateReviewRating() {
        int like5andDislike2 = reviewAndLikeService.calculateReviewRating(5, 2);
        int like3andDislike7 = reviewAndLikeService.calculateReviewRating(3, 7);
        int like0andDislike0 = reviewAndLikeService.calculateReviewRating(0, 0);
        int like0andDislike1 = reviewAndLikeService.calculateReviewRating(0, 1);
        int like2andDislike0 = reviewAndLikeService.calculateReviewRating(2, 0);

        assertEquals(4, like5andDislike2);
        assertEquals(2, like3andDislike7);
        assertEquals(0, like0andDislike0);
        assertEquals(1, like0andDislike1);
        assertEquals(5, like2andDislike0);
    }
}
