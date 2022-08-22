package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.author.AuthorEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.AuthorDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    public List<BookDto> getBooksData() {
        Map<Integer, String> authors = new HashMap<>();
        Map<String, List<AuthorDto>> authorsMapGrouped = authorService.getAuthorsGroupedMap();
        for (List<AuthorDto> authorsList : authorsMapGrouped.values()) {
            for (AuthorDto authorDto : authorsList) {
                Integer authorId = authorDto.getId();
                String authorName = authorDto.getName();
                authors.put(authorId, authorName);
            }
        }

        List<BookEntity> booksList = bookRepository.findAll();
        List<BookDto> booksDtoList = new ArrayList<>();
        for (BookEntity book : booksList) {
            BookDto bookDto = new BookDto();
            bookDto.setId(book.getId());
            bookDto.setAuthor("DEFAULT NAME");
            bookDto.setTitle(book.getTitle());
            int price = book.getPrice();
            bookDto.setPrice(price);
            int priceOld = (price * (100 + book.getDiscount())) / 100;
            bookDto.setPriceOld(priceOld);
            booksDtoList.add(bookDto);
        }

        return booksDtoList;
    }
}
