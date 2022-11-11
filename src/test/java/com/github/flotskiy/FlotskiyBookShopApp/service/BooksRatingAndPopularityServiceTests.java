package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.exceptions.RateBookByUserException;
import com.github.flotskiy.FlotskiyBookShopApp.exceptions.RateBookReviewException;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.PopularityDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.page.DetailedRatingDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.review.BookRatingEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.review.BookReviewEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.review.BookReviewLikeEntity;
import com.github.flotskiy.FlotskiyBookShopApp.repository.*;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BooksRatingAndPopularityServiceTests {

    @InjectMocks
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;

    @MockBean
    private final BookRatingRepository bookRatingRepositoryMock;

    @MockBean
    private final Book2UserRepository book2UserRepositoryMock;

    @MockBean
    private final BookRepository bookRepositoryMock;

    @MockBean
    private final BookReviewRepository bookReviewRepositoryMock;

    @MockBean
    private final BookReviewLikeRepository bookReviewLikeRepositoryMock;

    private BookRatingEntity bookRating1;
    private BookRatingEntity bookRating2;
    private BookRatingEntity bookRating3;
    private BookRatingEntity bookRating4;
    private BookRatingEntity bookRating5;
    private BookRatingEntity bookRating6;
    private BookRatingEntity bookRating7;
    private BookRatingEntity bookRating8;
    private BookRatingEntity bookRating9;

    private Book2UserEntity book2UserEntity1;
    private Book2UserEntity book2UserEntity2;
    private Book2UserEntity book2UserEntity3;
    private Book2UserEntity book2UserEntity4;

    private BookEntity bookEntity;
    private Optional<BookEntity> bookEntityOptional;
    private Optional<BookReviewEntity> bookReviewEntityOptional;
    private BookReviewLikeEntity bookReviewLikeEntity;

    @Autowired
    public BooksRatingAndPopularityServiceTests(
            BooksRatingAndPopularityService booksRatingAndPopularityService,
            BookRatingRepository bookRatingRepositoryMock,
            Book2UserRepository book2UserRepositoryMock,
            BookRepository bookRepositoryMock,
            BookReviewRepository bookReviewRepositoryMock,
            BookReviewLikeRepository bookReviewLikeRepositoryMock
    ) {
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.bookRatingRepositoryMock = bookRatingRepositoryMock;
        this.book2UserRepositoryMock = book2UserRepositoryMock;
        this.bookRepositoryMock = bookRepositoryMock;
        this.bookReviewRepositoryMock = bookReviewRepositoryMock;
        this.bookReviewLikeRepositoryMock = bookReviewLikeRepositoryMock;
    }

    @BeforeEach
    void setUp() {
        bookRating1 = new BookRatingEntity();
        bookRating1.setBookId(1);
        bookRating1.setUserId(1);
        bookRating1.setRating((short) 5);

        bookRating2 = new BookRatingEntity();
        bookRating2.setBookId(1);
        bookRating2.setUserId(2);
        bookRating2.setRating((short) 3);

        bookRating3 = new BookRatingEntity();
        bookRating3.setBookId(1);
        bookRating3.setUserId(3);
        bookRating3.setRating((short) 4);

        bookRating4 = new BookRatingEntity();
        bookRating4.setBookId(2);
        bookRating4.setUserId(1);
        bookRating4.setRating((short) 1);

        bookRating5 = new BookRatingEntity();
        bookRating5.setBookId(3);
        bookRating5.setUserId(1);
        bookRating5.setRating((short) 3);

        bookRating6 = new BookRatingEntity();
        bookRating6.setBookId(3);
        bookRating6.setUserId(2);
        bookRating6.setRating((short) 5);

        bookRating7 = new BookRatingEntity();
        bookRating7.setBookId(3);
        bookRating7.setUserId(3);
        bookRating7.setRating((short) 5);

        bookRating8 = new BookRatingEntity();
        bookRating8.setBookId(3);
        bookRating8.setUserId(4);
        bookRating8.setRating((short) 2);

        bookRating9 = new BookRatingEntity();
        bookRating9.setBookId(3);
        bookRating9.setUserId(5);
        bookRating9.setRating((short) 4);

        Mockito.doReturn(Arrays.asList(bookRating1, bookRating2, bookRating3))
                .when(bookRatingRepositoryMock).findBookRatingEntitiesByBookId(1);
        Mockito.doReturn(Arrays.asList(bookRating4))
                .when(bookRatingRepositoryMock).findBookRatingEntitiesByBookId(2);
        Mockito.doReturn(Arrays.asList(bookRating5, bookRating6, bookRating7, bookRating8, bookRating9))
                .when(bookRatingRepositoryMock).findBookRatingEntitiesByBookId(3);

        book2UserEntity1 = new Book2UserEntity();
        book2UserEntity1.setBookId(1);
        book2UserEntity1.setUserId(1);
        book2UserEntity1.setTypeId(3);

        book2UserEntity2 = new Book2UserEntity();
        book2UserEntity2.setBookId(2);
        book2UserEntity2.setUserId(1);
        book2UserEntity2.setTypeId(2);

        book2UserEntity3 = new Book2UserEntity();
        book2UserEntity3.setBookId(2);
        book2UserEntity3.setUserId(2);
        book2UserEntity3.setTypeId(3);

        book2UserEntity4 = new Book2UserEntity();
        book2UserEntity4.setBookId(2);
        book2UserEntity4.setUserId(3);
        book2UserEntity4.setTypeId(1);

        Mockito.doReturn(Arrays.asList(book2UserEntity1, book2UserEntity2, book2UserEntity3, book2UserEntity4))
                .when(book2UserRepositoryMock).findAll();

        bookEntity = new BookEntity();
        bookEntity.setId(1);
        bookEntityOptional = Optional.of(bookEntity);

        Mockito.doReturn(bookEntityOptional).when(bookRepositoryMock).findById(1);

        BookReviewEntity bookReviewEntity = new BookReviewEntity();
        bookReviewEntity.setId(1);
        bookReviewEntity.setBookId(1);
        bookReviewEntityOptional = Optional.of(bookReviewEntity);

        Mockito.doReturn(bookReviewEntityOptional).when(bookReviewRepositoryMock).findById(1);
    }

    @AfterEach
    void tearDown() {
        bookRating1 = null;
        bookRating2 = null;
        bookRating3 = null;
        bookRating4 = null;
        bookRating5 = null;
        bookRating6 = null;
        bookRating7 = null;
        bookRating8 = null;
        bookRating9 = null;
        book2UserEntity1 = null;
        book2UserEntity2 = null;
        book2UserEntity3 = null;
        book2UserEntity4 = null;
        bookEntity = null;
        bookEntityOptional = Optional.empty();
        bookReviewEntityOptional = Optional.empty();
        bookReviewLikeEntity = null;
    }

    @Test
    void calculateBookRating() {
        int ratingOfBook1 = booksRatingAndPopularityService.calculateBookRating(1);
        int ratingOfBook2 = booksRatingAndPopularityService.calculateBookRating(2);
        int ratingOfBook3 = booksRatingAndPopularityService.calculateBookRating(3);

        assertEquals(4, ratingOfBook1);
        assertEquals(1, ratingOfBook2);
        assertEquals(4, ratingOfBook3);
        Mockito.verify(bookRatingRepositoryMock, Mockito.times(3))
                .findBookRatingEntitiesByBookId(Mockito.any(Integer.class));
    }

    @Test
    void getDetailedRatingDto() {
        DetailedRatingDto detailedRatingDto = booksRatingAndPopularityService.getDetailedRatingDto(3);
        assertNotNull(detailedRatingDto);
        assertEquals(5, detailedRatingDto.getCount());
        assertEquals(2, detailedRatingDto.getFiveStarsCount());
        assertEquals(1, detailedRatingDto.getFourStarsCount());
        Mockito.verify(bookRatingRepositoryMock, Mockito.times(1))
                .findBookRatingEntitiesByBookId(Mockito.any(Integer.class));
    }

    @Test
    void getAllPopularBooks() {
        List<PopularityDto> popularityDtoList = booksRatingAndPopularityService.getAllPopularBooks();
        assertNotNull(popularityDtoList);
        assertEquals(2, popularityDtoList.get(0).getBookId());
        assertEquals((short) 3, popularityDtoList.get(0).getPopularity());
        assertEquals(1, popularityDtoList.get(1).getBookId());
        assertEquals((short) 1, popularityDtoList.get(1).getPopularity());
        Mockito.verify(book2UserRepositoryMock, Mockito.times(1)).findAll();
    }

    @Test
    void setRatingToBookByUser() throws RateBookByUserException {
        Mockito.doReturn(bookRating1).when(bookRatingRepositoryMock).save(Mockito.any(BookRatingEntity.class));

        BookRatingEntity bookRatingEntity =
                booksRatingAndPopularityService.setRatingToBookByUser(1, 1, 5);

        assertNotNull(bookRatingEntity);
        assertEquals(1, bookRatingEntity.getBookId());
        assertEquals(1, bookRatingEntity.getUserId());
        assertEquals((short) 5, bookRatingEntity.getRating());
        Mockito.verify(bookRatingRepositoryMock, Mockito.times(1))
                .save(Mockito.any(BookRatingEntity.class));
    }

    @Test
    void rateBookReview() throws RateBookReviewException {
        bookReviewLikeEntity = new BookReviewLikeEntity();
        bookReviewLikeEntity.setReviewId(1);
        bookReviewLikeEntity.setValue((short) 1);

        Mockito.doReturn(bookReviewLikeEntity).when(bookReviewLikeRepositoryMock)
                .save(Mockito.any(BookReviewLikeEntity.class));

        BookReviewLikeEntity bookReviewLikeEntity =
                booksRatingAndPopularityService.rateBookReview(1, 1, 1);
        assertNotNull(bookReviewLikeEntity);
        assertEquals(1, bookReviewLikeEntity.getReviewId());
        assertEquals((short) 1, bookReviewLikeEntity.getValue());
        Mockito.verify(bookReviewLikeRepositoryMock, Mockito.times(1))
                .save(Mockito.any(BookReviewLikeEntity.class));
    }
}
