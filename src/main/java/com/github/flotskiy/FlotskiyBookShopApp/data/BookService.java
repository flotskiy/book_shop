package com.github.flotskiy.FlotskiyBookShopApp.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookService {

    private JdbcTemplate jdbcTemplate;
    private AuthorsService authorsService;

    @Autowired
    public BookService(JdbcTemplate jdbcTemplate, AuthorsService authorsService) {
        this.jdbcTemplate = jdbcTemplate;
        this.authorsService = authorsService;
    }

    public List<Book> getBooksData() {
        Map<Integer, String> authorsMap = new HashMap<>();
        for (Author author : authorsService.getAuthorsData()) {
            Integer authorId = author.getId();
            String authorName = author.getAuthor();
            authorsMap.put(authorId, authorName);
        }

        List<Book> books = jdbcTemplate.query("SELECT * FROM books", (ResultSet rs, int rowNum) -> {
            Book book = new Book();
            book.setId(rs.getInt("id"));
            String authorName = authorsMap.get(rs.getInt("author_id"));
            book.setAuthor(authorName);
            book.setTitle(rs.getString("title"));
            book.setPriceOld(rs.getString("priceOld"));
            book.setPrice(rs.getString("price"));
            return book;
        });
        return new ArrayList<>(books);
    }
}
