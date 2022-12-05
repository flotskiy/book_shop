package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserBooksData;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.author.AuthorEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.review.BookRatingEntity;
import com.github.flotskiy.FlotskiyBookShopApp.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookServiceTests {

    @InjectMocks
    private final BookService bookService;

    @MockBean
    private final BookRepository bookRepositoryMock;

    @MockBean
    private final BookRatingRepository bookRatingRepositoryMock;

    @MockBean
    private final Book2AuthorRepository book2AuthorRepositoryMock;

    private BookEntity bookEntity1;
    private BookEntity bookEntity2;
    private BookEntity bookEntity3;
    private BookEntity bookEntity4;
    private BookEntity bookEntity5;
    private BookEntity bookEntity6;
    private BookEntity bookEntity7;

    private AuthorEntity author1;
    Set<AuthorEntity> authorsSet1;
    private AuthorEntity author2;
    Set<AuthorEntity> authorsSet2;

    private BookRatingEntity bookRating1;
    private BookRatingEntity bookRating2;
    private BookRatingEntity bookRating3;
    private BookRatingEntity bookRating4;
    private BookRatingEntity bookRating5;
    private BookRatingEntity bookRating6;
    private BookRatingEntity bookRating7;
    private BookRatingEntity bookRating8;

    private UserDto user;
    private UserBooksData userBooksData;
    List<BookDto> paidBooks;
    BookDto bookDto;

    @Autowired
    public BookServiceTests(
            BookService bookService,
            BookRepository bookRepositoryMock,
            BookRatingRepository bookRatingRepositoryMock,
            Book2AuthorRepository book2AuthorRepositoryMock
    ) {
        this.bookService = bookService;
        this.bookRepositoryMock = bookRepositoryMock;
        this.bookRatingRepositoryMock = bookRatingRepositoryMock;
        this.book2AuthorRepositoryMock = book2AuthorRepositoryMock;
    }

    @BeforeEach
    void setUp() {
        author1 = new AuthorEntity();
        author1.setId(1);
        author1.setName("Author1");
        authorsSet1 = new HashSet<>();
        authorsSet1.add(author1);

        author2 = new AuthorEntity();
        author2.setId(2);
        author2.setName("Author2");
        authorsSet2 = new HashSet<>();
        authorsSet1.add(author2);

        bookEntity1 = new BookEntity();
        bookEntity1.setId(1);
        bookEntity1.setTitle("One");
        bookEntity1.setPubDate(LocalDate.now().minusDays(1));
        bookEntity1.setAuthorEntities(authorsSet1);

        bookEntity2 = new BookEntity();
        bookEntity2.setId(2);
        bookEntity2.setTitle("Two");
        bookEntity2.setPubDate(LocalDate.now().minusMonths(2));
        bookEntity2.setAuthorEntities(authorsSet2);

        bookEntity3 = new BookEntity();
        bookEntity3.setId(3);
        bookEntity3.setTitle("Three");
        bookEntity3.setPubDate(LocalDate.now().minusDays(5));
        bookEntity3.setAuthorEntities(authorsSet1);

        bookEntity4 = new BookEntity();
        bookEntity4.setId(4);
        bookEntity4.setTitle("Four");
        bookEntity4.setPubDate(LocalDate.now().minusDays(10));
        bookEntity4.setAuthorEntities(authorsSet1);

        bookEntity5 = new BookEntity();
        bookEntity5.setId(5);
        bookEntity5.setTitle("Five");
        bookEntity5.setPubDate(LocalDate.now().minusYears(1));
        bookEntity5.setAuthorEntities(authorsSet2);

        bookEntity6 = new BookEntity();
        bookEntity6.setId(6);
        bookEntity6.setTitle("Six");
        bookEntity6.setPubDate(LocalDate.now().minusMonths(3));
        bookEntity6.setAuthorEntities(authorsSet2);

        bookEntity7 = new BookEntity();
        bookEntity7.setId(7);
        bookEntity7.setTitle("Seven");
        bookEntity7.setPubDate(LocalDate.now().minusDays(5));
        bookEntity7.setAuthorEntities(authorsSet1);

        bookRating1 = new BookRatingEntity();
        bookRating1.setId(1);
        bookRating1.setBookId(1);
        bookRating1.setUserId(1);
        bookRating1.setRating((short) 5);

        bookRating2 = new BookRatingEntity();
        bookRating2.setId(2);
        bookRating2.setBookId(2);
        bookRating2.setUserId(1);
        bookRating2.setRating((short) 1);

        bookRating3 = new BookRatingEntity();
        bookRating3.setId(3);
        bookRating3.setBookId(3);
        bookRating3.setUserId(1);
        bookRating3.setRating((short) 2);

        bookRating4 = new BookRatingEntity();
        bookRating4.setId(4);
        bookRating4.setBookId(4);
        bookRating4.setUserId(1);
        bookRating4.setRating((short) 5);

        bookRating5 = new BookRatingEntity();
        bookRating5.setId(5);
        bookRating5.setBookId(4);
        bookRating5.setUserId(2);
        bookRating5.setRating((short) 3);

        bookRating6 = new BookRatingEntity();
        bookRating6.setId(6);
        bookRating6.setBookId(5);
        bookRating6.setUserId(1);
        bookRating6.setRating((short) 3);

        bookRating7 = new BookRatingEntity();
        bookRating7.setId(7);
        bookRating7.setBookId(5);
        bookRating7.setUserId(2);
        bookRating7.setRating((short) 4);

        bookRating8 = new BookRatingEntity();
        bookRating8.setId(8);
        bookRating8.setBookId(5);
        bookRating8.setUserId(3);
        bookRating8.setRating((short) 5);

        bookDto = new BookDto();
        bookDto.setId(1);
        paidBooks = new ArrayList<>();
        paidBooks.add(bookDto);
        userBooksData = new UserBooksData();
        userBooksData.setPaid(paidBooks);
        user = new UserDto();
        user.setUserBooksData(userBooksData);
    }

    @AfterEach
    void tearDown() {
        bookEntity1 = null;
        bookEntity2 = null;
        bookEntity3 = null;
        bookEntity4 = null;
        bookEntity5 = null;
        bookEntity6 = null;
        bookEntity7 = null;
        author1 = null;
        author2 = null;
        authorsSet1 = null;
        authorsSet2 = null;
        bookRating1 = null;
        bookRating2 = null;
        bookRating3 = null;
        bookRating4 = null;
        bookRating5 = null;
        bookRating6 = null;
        bookRating7 = null;
        bookRating8 = null;
        user = null;
        userBooksData = null;
        paidBooks = null;
        bookDto = null;
    }

    @Test
    void getListOfRecommendedBooksForGuestWithoutCartAndPostponed() {
        Mockito.doReturn(List.of(1, 4, 5)).when(bookRatingRepositoryMock).getFirst30bookIdsWithMaxUsersRatingMoreOrEquals4();
        Mockito.doReturn(Arrays.asList(bookEntity1, bookEntity5, bookEntity5))
                .when(bookRepositoryMock).findBookEntitiesByIdIsIn(Mockito.any(List.class));
        Mockito.doReturn(new PageImpl<>(Arrays.asList(bookEntity1, bookEntity3, bookEntity4, bookEntity7)))
                .when(bookRepositoryMock).findBookEntitiesByPubDateBetweenAndIdNotContainingOrderByPubDateDescIdDesc(
                        Mockito.any(LocalDate.class), Mockito.any(LocalDate.class), Mockito.any(Collection.class), Mockito.any(Pageable.class)
                );

        List<BookDto> guestRecommendedBooks = bookService.getListOfRecommendedBooks(0, 15, new UserDto());

        assertNotNull(guestRecommendedBooks);
        assertEquals(5, guestRecommendedBooks.size());
        assertThat(guestRecommendedBooks, containsInAnyOrder(
                hasProperty("title", is("One")),
                hasProperty("title", is("Three")),
                hasProperty("title", is("Four")),
                hasProperty("title", is("Five")),
                hasProperty("title", is("Seven"))
        ));
        assertThat(guestRecommendedBooks, not(containsInAnyOrder(
                hasProperty("title", is("Two")),
                hasProperty("title", is("Six"))
        )));
        Mockito.verify(bookRatingRepositoryMock, Mockito.times(1))
                .getFirst30bookIdsWithMaxUsersRatingMoreOrEquals4();
        Mockito.verify(bookRepositoryMock, Mockito.times(1))
                .findBookEntitiesByIdIsIn(Mockito.any(List.class));
        Mockito.verify(bookRepositoryMock, Mockito.times(1))
                .findBookEntitiesByPubDateBetweenAndIdNotContainingOrderByPubDateDescIdDesc(
                        Mockito.any(LocalDate.class), Mockito.any(LocalDate.class), Mockito.any(Collection.class), Mockito.any(Pageable.class)
                );
    }

    @Test
    void getListOfRecommendedBooksForUser() {
        Mockito.doReturn(List.of(1, 3, 4, 7)).when(book2AuthorRepositoryMock)
                .getBookIdsForAuthorIdsInList(Mockito.any(List.class));
        Mockito.doReturn(new PageImpl<>(Arrays.asList(bookEntity3, bookEntity4, bookEntity7)))
                .when(bookRepositoryMock)
                .findBookEntitiesByIdIsInOrderByPubDageDesc(Mockito.any(List.class), Mockito.any(Pageable.class));

        List<BookDto> userRecommendedBooks = bookService.getListOfRecommendedBooks(0, 15, user);
        assertNotNull(userRecommendedBooks);
        assertEquals(3, userRecommendedBooks.size());
        assertThat(userRecommendedBooks, containsInAnyOrder(
                hasProperty("title", is("Three")),
                hasProperty("title", is("Four")),
                hasProperty("title", is("Seven"))
        ));
        assertThat(userRecommendedBooks, not(containsInAnyOrder(
                hasProperty("title", is("One")),
                hasProperty("title", is("Two")),
                hasProperty("title", is("Five")),
                hasProperty("title", is("Six"))
        )));
        Mockito.verify(book2AuthorRepositoryMock, Mockito.times(1))
                .getBookIdsForAuthorIdsInList(Mockito.any(List.class));
        Mockito.verify(bookRepositoryMock, Mockito.times(1))
                .findBookEntitiesByIdIsInOrderByPubDageDesc(Mockito.any(List.class), Mockito.any(Pageable.class));
    }
}
