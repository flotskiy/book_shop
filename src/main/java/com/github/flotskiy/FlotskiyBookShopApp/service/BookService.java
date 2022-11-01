package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.exceptions.BookstoreApiWrongParameterException;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.AuthorDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.PopularityDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.page.BookFileDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.page.BookSlugDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.author.AuthorEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookTagEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.file.BookFileEntity;
import com.github.flotskiy.FlotskiyBookShopApp.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final TagService tagService;
    private final AuthorService authorService;
    private final ReviewAndLikeService reviewAndLikeService;
    private final Book2UserService book2UserService;

    @Autowired
    public BookService(
            BookRepository bookRepository,
            BooksRatingAndPopularityService booksRatingAndPopularityService,
            TagService tagService,
            AuthorService authorService,
            ReviewAndLikeService reviewAndLikeService,
            Book2UserService book2UserService
    ) {
        this.bookRepository = bookRepository;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.tagService = tagService;
        this.authorService = authorService;
        this.reviewAndLikeService = reviewAndLikeService;
        this.book2UserService = book2UserService;
    }

    public List<BookDto> getAllBooksData(Integer userId) {
        return convertBookEntitiesToBookDtoList(bookRepository.findAll(), userId);
    }

    public Page<BookDto> getPageOfBooks(int offset, int limit, int userId) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntities = bookRepository.findAll(nextPage);
        return bookEntities.map(bookEntity -> convertBookEntityToBookDto(bookEntity, userId));
    }

    public List<BookDto> getBooksByAuthor(String authorName, Integer userId) {
        return convertBookEntitiesToBookDtoList(bookRepository.findBookEntitiesByAuthorEntitiesNameContaining(authorName), userId);
    }

    public List<BookDto> getBooksByTitle(String title, Integer userId) throws BookstoreApiWrongParameterException {
        if (title.length() <= 1) {
            throw new BookstoreApiWrongParameterException("Wrong value passed to parameter");
        }
        List<BookDto> data = convertBookEntitiesToBookDtoList(bookRepository.findBookEntitiesByTitleContaining(title), userId);
        if (data.size() < 1) {
            throw new BookstoreApiWrongParameterException("No data found with specified parameter...");
        }
        return data;
    }

    public List<BookDto> getBooksWithPriceBetween(int min, int max, Integer userId) {
        return convertBookEntitiesToBookDtoList(bookRepository.findBookEntitiesByPriceBetween(min, max), userId);
    }

    public List<BookDto> getBooksWithPrice(int price, Integer userId) {
        return convertBookEntitiesToBookDtoList(bookRepository.findBookEntitiesByPriceIs(price), userId);
    }

    public List<BookDto> getBooksWithMaxDiscount(Integer userId) {
        return convertBookEntitiesToBookDtoList(bookRepository.getBookEntitiesWithMaxDiscount(), userId);
    }

    public List<BookDto> getBestsellers(Integer userId) {
        return convertBookEntitiesToBookDtoList(bookRepository.getBestsellers(), userId);
    }

    public Page<BookDto> getPageOfSearchResultBooks(String searchWord, int offset, int limit, int userId) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntities = bookRepository.findBookEntitiesByTitleContaining(searchWord, nextPage);
        return bookEntities.map(bookEntity -> convertBookEntityToBookDto(bookEntity, userId));
    }

    public Integer getSearchResultsSize(String searchWord) {
        return bookRepository.countBookEntitiesByTitleContaining(searchWord);
    }

    public Page<BookDto> getPageOfRecentBooks(LocalDate from, LocalDate to, int offset, int limit, int userId) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntities =
                bookRepository.findBookEntitiesByPubDateBetweenOrderByPubDateDesc(from, to, nextPage);
        return bookEntities.map(bookEntity -> convertBookEntityToBookDto(bookEntity, userId));
    }

    public List<BookDto> getRecentBooks(int offset, int limit, int userId) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusMonths(1);
        return getPageOfRecentBooks(from, to, offset, limit, userId).getContent();
    }

    public List<BookDto> getPopularBooks(int offset, int limit, int userId) {
        List<PopularityDto> popularityDtoList = booksRatingAndPopularityService.getAllPopularBooks();
        List<Integer> bookIds = booksRatingAndPopularityService.getPopularBookIds(popularityDtoList, offset, limit);
        List<BookEntity> bookEntities = bookRepository.findBookEntitiesByIdIsIn(bookIds);
        List<BookDto> result = new ArrayList<>();
        for (BookEntity book : bookEntities) {
            result.add(convertBookEntityToBookDtoWithPopularityValue(book, popularityDtoList, userId));
        }
        result.sort(Comparator.comparing(BookDto::getRating).reversed());
        return result;
    }

    public Page<BookDto> getPageOfBooksByTag(Integer tagId, Integer offset, Integer limit, Integer userId) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntities = bookRepository.findBookEntitiesByBookTagsIdOrderByPubDateDesc(tagId, nextPage);
        return bookEntities.map(bookEntity -> convertBookEntityToBookDto(bookEntity, userId));
    }

    public Page<BookDto> getPageOfBooksByGenreId(Integer genreId, Integer offset, Integer limit, Integer userId) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntities =
                bookRepository.findBookEntitiesByGenreEntitiesIdOrderByPubDateDesc(genreId, nextPage);
        return bookEntities.map(bookEntity -> convertBookEntityToBookDto(bookEntity, userId));
    }

    public Page<BookDto> getPageOfBooksByAuthorId(Integer authorId, Integer offset, Integer limit, Integer userId) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntities =
                bookRepository.findBookEntitiesByAuthorEntitiesIdOrderByPubDateDesc(authorId, nextPage);
        return bookEntities.map(bookEntity -> convertBookEntityToBookDto(bookEntity, userId));
    }

    public List<BookDto> getBooksBySlugIn(List<String> slugs, Integer userId) {
        return convertBookEntitiesToBookDtoWithRatingList(bookRepository.findBookEntitiesBySlugIn(slugs), userId);
    }

    public BookEntity getBookEntityBySlug(String slug) {
        return bookRepository.findBookEntityBySlug(slug);
    }

    public void saveBookEntity(BookEntity bookEntity) {
        bookRepository.save(bookEntity);
    }

    public BookSlugDto getBookSlugBySlug(String slug, Integer userId) {
        BookEntity bookEntity = bookRepository.findBookEntityBySlug(slug);
        BookSlugDto bookSlugDto = new BookSlugDto(convertBookEntityToBookDtoWithRatingValue(bookEntity, userId));
        bookSlugDto.setDescription(bookEntity.getDescription());
        Set<BookTagEntity> tagEntities = bookEntity.getBookTags();
        Set<AuthorDto> authorDtos = bookEntity.getAuthorEntities().stream()
                .map(authorService::convertAuthorEntityToAuthorDtoShort).collect(Collectors.toSet());
        bookSlugDto.setAuthors(authorDtos);
        bookSlugDto.setTags(tagService.convertTagEntitySetToTagDtoSet(tagEntities));
        Set<BookFileDto> bookFileDtos = bookEntity.getBookFileEntities().stream()
                .map(this::convertBookFileEntityToBookFileDto).collect(Collectors.toSet());
        bookSlugDto.setBookFileDtos(bookFileDtos);
        bookSlugDto.setBookReviewDtos(reviewAndLikeService.getBookReviewDtos(bookEntity.getId()));
        return bookSlugDto;
    }

    public String checkFrom(String from) {
        if (from == null || from.isEmpty()) {
            return "01.01.1900";
        }
        return from;
    }

    public String checkTo(String to) {
        if (to == null || to.isEmpty()) {
            to = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        }
        return to;
    }

    public List<BookEntity> findBookEntitiesByIdIsIn(List<Integer> bookIdList) {
        return bookRepository.findBookEntitiesByIdIsIn(bookIdList);
    }

    public List<BookDto> convertBookEntitiesToBookDtoWithRatingList(List<BookEntity> booksListToConvert, Integer userId) {
        List<BookDto> booksDtoList = new ArrayList<>();
        for (BookEntity book : booksListToConvert) {
            booksDtoList.add(convertBookEntityToBookDtoWithRatingValue(book, userId));
        }
        return booksDtoList;
    }

    private List<BookDto> convertBookEntitiesToBookDtoList(List<BookEntity> booksListToConvert, Integer userId) {
        List<BookDto> booksDtoList = new ArrayList<>();
        for (BookEntity book : booksListToConvert) {
            booksDtoList.add(convertBookEntityToBookDto(book, userId));
        }
        return booksDtoList;
    }

    private BookDto convertBookEntityToBookDto(BookEntity bookEntity, Integer userId) {
        BookDto bookDto = new BookDto();
        int bookId = bookEntity.getId();
        bookDto.setId(bookId);
        bookDto.setSlug(bookEntity.getSlug());
        bookDto.setImage(bookEntity.getImage());
        bookDto.setTitle(bookEntity.getTitle());
        short discount = bookEntity.getDiscount();
        bookDto.setDiscount(discount);
        bookDto.setIsBestseller(bookEntity.getIsBestseller() == 1);
        int price = bookEntity.getPrice();
        bookDto.setPrice(price);
        int discountPrice = (int) (price * ((100 - (double) discount) / 100));
        bookDto.setDiscountPrice(discountPrice);
        bookDto.setPubDate(bookEntity.getPubDate());

        Set<AuthorEntity> authorEntities = bookEntity.getAuthorEntities();
        StringBuilder authorName = new StringBuilder();
        for (AuthorEntity author : authorEntities) {
            authorName.append(author.getName()).append(", ");
        }
        if (authorName.length() > 0) {
            authorName.delete(authorName.length() - ", ".length(), authorName.length());
        }
        if (authorName.length() == 0) {
            authorName = new StringBuilder("BOOK HAS NO AUTHOR");
        }
        bookDto.setAuthors(authorName.toString());

        bookDto.setStatus(book2UserService.getBookStatus(bookDto.getId(), userId));
        bookDto.setRating((short) 0);

        return bookDto;
    }

    private BookDto convertBookEntityToBookDtoWithRatingValue(BookEntity bookEntity, Integer userId) {
        BookDto bookDto = convertBookEntityToBookDto(bookEntity, userId);
        short rating = booksRatingAndPopularityService.calculateBookRating(bookEntity.getId()).shortValue();
        bookDto.setRating(rating);
        return bookDto;
    }

    private BookDto convertBookEntityToBookDtoWithPopularityValue(
            BookEntity bookEntity, List<PopularityDto> popularityDtoList, Integer userId) {
        BookDto bookDto = convertBookEntityToBookDto(bookEntity, userId);

        short rating = 0;
        for (PopularityDto popularityDto : popularityDtoList) {
            if (bookEntity.getId() == popularityDto.getBookId()) {
                rating = popularityDto.getPopularity();
            }
        }
        bookDto.setRating(rating);

        return bookDto;
    }

    private BookFileDto convertBookFileEntityToBookFileDto(BookFileEntity bookFileEntity) {
        BookFileDto bookFileDto = new BookFileDto();
        bookFileDto.setHash(bookFileEntity.getHash());
        bookFileDto.setTypeId(bookFileEntity.getTypeId());
        bookFileDto.setPath(bookFileEntity.getPath());
        return bookFileDto;
    }
}
