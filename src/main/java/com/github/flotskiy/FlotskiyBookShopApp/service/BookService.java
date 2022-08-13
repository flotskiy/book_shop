package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.dto.AuthorDto;
import com.github.flotskiy.FlotskiyBookShopApp.dto.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.repository.BookShopRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookService {

    private BookShopRepo bookShopRepo;
    private AuthorsService authorsService;

    @Autowired
    public BookService(BookShopRepo bookShopRepo, AuthorsService authorsService) {
        this.bookShopRepo = bookShopRepo;
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

        List<BookDto> booksDto = bookShopRepo.getJdbcTemplate()
                .query("SELECT * FROM books", (ResultSet rs, int rowNum) -> {
            BookDto bookDto = new BookDto();
            bookDto.setId(rs.getInt("id"));
            String authorName = authors.get(rs.getInt("author_id"));
            bookDto.setAuthor(authorName);
            bookDto.setTitle(rs.getString("title"));
            bookDto.setPriceOld(rs.getString("price_old"));
            bookDto.setPrice(rs.getString("price"));
            return bookDto;
        });
        return new ArrayList<>(booksDto);
    }
}
