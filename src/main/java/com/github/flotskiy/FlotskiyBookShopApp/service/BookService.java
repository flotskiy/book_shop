package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.data.Author;
import com.github.flotskiy.FlotskiyBookShopApp.data.Book;
import com.github.flotskiy.FlotskiyBookShopApp.dto.AuthorDto;
import com.github.flotskiy.FlotskiyBookShopApp.dto.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookService {

    private BookRepository bookRepository;
    private AuthorsService authorsService;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorsService authorsService) {
        this.bookRepository = bookRepository;
        this.authorsService = authorsService;
    }

    public List<BookDto> getBooksData() {
        Map<Integer, String> authors = new HashMap<>();
        Map<String, List<AuthorDto>> authorsMapGrouped = authorsService.getAuthorsMap();
        for (List<AuthorDto> authorsList : authorsMapGrouped.values()) {
            for (AuthorDto authorDto : authorsList) {
                Integer authorId = authorDto.getId();
                String authorName = authorDto.getName();
                authors.put(authorId, authorName);
            }
        }

        List<Book> booksList = bookRepository.findAll();
        List<BookDto> booksDtoList = new ArrayList<>();
        for (Book book : booksList) {
            BookDto bookDto = new BookDto();
            bookDto.setId(book.getId());
            Author author = book.getAuthor();
            String authorName = author.getFirstName() + " " + author.getLastName();
            bookDto.setAuthor(authorName);
            bookDto.setTitle(book.getTitle());
            bookDto.setPriceOld(book.getPriceOld());
            bookDto.setPrice(book.getPrice());
            booksDtoList.add(bookDto);
        }

        return booksDtoList;
    }
}
