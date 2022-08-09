package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.dto.AuthorDto;
import com.github.flotskiy.FlotskiyBookShopApp.repository.BookShopRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorsService {

    private BookShopRepo bookShopRepo;

    @Autowired
    public AuthorsService(BookShopRepo bookShopRepo) {
        this.bookShopRepo = bookShopRepo;
    }

    public Map<String, List<AuthorDto>> getAuthorsMap() {
        List<AuthorDto> authors = bookShopRepo.getJdbcTemplate()
                .query("SELECT * FROM authors", (ResultSet rs, int rowNum) -> {
            AuthorDto authorDto = new AuthorDto();
            authorDto.setId(rs.getInt("id"));
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            authorDto.setName(lastName + " " + firstName);
            return authorDto;
        });

        return authors.stream()
                .collect(Collectors.groupingBy((AuthorDto a) -> a.getName().substring(0,1).toUpperCase()));
    }
}
