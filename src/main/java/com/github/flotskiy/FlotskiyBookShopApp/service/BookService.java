package com.github.flotskiy.FlotskiyBookShopApp.service;

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

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
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

    public List<BookDto> getRecentBooksDefault(int offset, int limit) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusMonths(1);
        return getPageOfRecentBooks(from, to, offset, limit).getContent();
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
        bookDto.setId(bookEntity.getId());
        bookDto.setPubDate(bookEntity.getPubDate());
        bookDto.setIsBestseller(bookEntity.getIsBestseller());
        Set<AuthorEntity> authorEntities = bookEntity.getAuthorEntities();
        String authorName = "BOOK HAS NO AUTHOR";
        if (authorEntities.iterator().hasNext()) {
            authorName = authorEntities.iterator().next().getName();
        }
        bookDto.setAuthor(authorName);
        bookDto.setTitle(bookEntity.getTitle());
        bookDto.setImage(bookEntity.getImage());
        int price = bookEntity.getPrice();
        bookDto.setPrice(price);
        int priceOld = (price * (100 + bookEntity.getDiscount())) / 100;
        bookDto.setPriceOld(priceOld);
        bookDto.setDiscount(bookEntity.getDiscount());
        return bookDto;
    }
}
