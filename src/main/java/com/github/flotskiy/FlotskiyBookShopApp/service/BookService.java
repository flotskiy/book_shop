package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.RatingDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.author.AuthorEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;

    @Autowired
    public BookService(BookRepository bookRepository, BooksRatingAndPopularityService booksRatingAndPopularityService) {
        this.bookRepository = bookRepository;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
    }

    public List<BookDto> getAllBooksData() {
        return convertBookEntitiesToBookDtoList(bookRepository.findAll());
    }

    public Page<BookDto> getPageOfBooks(int offset, int limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntities = bookRepository.findAll(nextPage);
        return bookEntities.map(this::convertBookEntityToBookDto);
    }

    public List<BookDto> getBooksByAuthor(String authorName) {
        return convertBookEntitiesToBookDtoList(bookRepository.findBookEntitiesByAuthorEntitiesNameContaining(authorName));
    }

    public List<BookDto> getBooksByTitle(String title) {
        return convertBookEntitiesToBookDtoList(bookRepository.findBookEntitiesByTitleContaining(title));
    }

    public List<BookDto> getBooksWithPriceBetween(int min, int max) {
        return convertBookEntitiesToBookDtoList(bookRepository.findBookEntitiesByPriceBetween(min, max));
    }

    public List<BookDto> getBooksWithPrice(int price) {
        return convertBookEntitiesToBookDtoList(bookRepository.findBookEntitiesByPriceIs(price));
    }

    public List<BookDto> getBooksWithMaxDiscount() {
        return convertBookEntitiesToBookDtoList(bookRepository.getBookEntitiesWithMaxDiscount());
    }

    public List<BookDto> getBestsellers() {
        return convertBookEntitiesToBookDtoList(bookRepository.getBestsellers());
    }

    public Page<BookDto> getPageOfSearchResultBooks(String searchWord, int offset, int limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntities = bookRepository.findBookEntitiesByTitleContaining(searchWord, nextPage);
        return bookEntities.map(this::convertBookEntityToBookDto);
    }

    public Page<BookDto> getPageOfRecentBooks(LocalDate from, LocalDate to, int offset, int limit) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<BookEntity> bookEntities =
                bookRepository.findBookEntitiesByPubDateBetweenOrderByPubDateDesc(from, to, nextPage);
        return bookEntities.map(this::convertBookEntityToBookDto);
    }

    public List<BookDto> getRecentBooks(int offset, int limit) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusMonths(1);
        return getPageOfRecentBooks(from, to, offset, limit).getContent();
    }

    public List<BookDto> getPageOfPopularBooks(int offset, int limit) {
        List<RatingDto> ratingDtoList = booksRatingAndPopularityService.getAllBooksRating();
        ratingDtoList.forEach(System.out::println); // todo - remove string
        List<Integer> bookIds = booksRatingAndPopularityService.getPopularBookIds(ratingDtoList, offset, limit);
        List<BookEntity> bookEntities = bookRepository.findBookEntitiesByIdIsIn(bookIds);
        List<BookDto> result = new ArrayList<>();
        for (BookEntity book : bookEntities) {
            result.add(convertBookEntityToBookDtoWithActualRatingValue(book, ratingDtoList));
        }
        result.sort(Comparator.comparing(BookDto::getRating).reversed());
        return result;
    }

    private List<BookDto> convertBookEntitiesToBookDtoList(List<BookEntity> booksListToConvert) {
        List<BookDto> booksDtoList = new ArrayList<>();
        for (BookEntity book : booksListToConvert) {
            booksDtoList.add(convertBookEntityToBookDto(book));
        }
        return booksDtoList;
    }

    private BookDto convertBookEntityToBookDto(BookEntity bookEntity) {
        BookDto bookDto = new BookDto();
        int bookId = bookEntity.getId();
        bookDto.setId(bookId);
        bookDto.setSlug(bookEntity.getSlug());
        bookDto.setImage(bookEntity.getImage());
        bookDto.setTitle(bookEntity.getTitle());
        short discount = bookEntity.getDiscount();
        bookDto.setDiscount(discount);
        bookDto.setBestseller(bookEntity.getIsBestseller() == 1);
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

        bookDto.setStatus("false"); // TODO later
        bookDto.setRating((short) 0);

        return bookDto;
    }

    private BookDto convertBookEntityToBookDtoWithActualRatingValue(BookEntity bookEntity, List<RatingDto> ratingDtoList) {
        BookDto bookDto = convertBookEntityToBookDto(bookEntity);

        short rating = 0;
        for (RatingDto ratingDto : ratingDtoList) {
            if (bookEntity.getId() == ratingDto.getBookId()) {
                rating = ratingDto.getRating();
            }
        }
        bookDto.setRating(rating);

        return bookDto;
    }
}
