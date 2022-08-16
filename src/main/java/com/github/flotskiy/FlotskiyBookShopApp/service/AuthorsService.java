package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.data.Author;
import com.github.flotskiy.FlotskiyBookShopApp.dto.AuthorDto;
import com.github.flotskiy.FlotskiyBookShopApp.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorsService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorsService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<AuthorDto> getAuthorsMap() {
        List<Author> authorList = authorRepository.findAll();
        List<AuthorDto> authorDtoList = new ArrayList<>();
        for (Author author : authorList) {
            AuthorDto authorDto = new AuthorDto();
            authorDto.setId(author.getId());
            String firstName = author.getFirstName();
            String lastName = author.getLastName();
            authorDto.setName(lastName + " " + firstName);
            authorDtoList.add(authorDto);
        }
        return authorDtoList;
    }

    public Map<String, List<AuthorDto>> getAuthorsGroupedMap() {
        List<AuthorDto> authorDtoList = getAuthorsMap();
        return authorDtoList.stream()
                .peek(authorDto -> {
                    String[] nameArray = authorDto.getName().split(" ", 2);
                    authorDto.setName(nameArray[0] + " " + nameArray[1]);
                })
                .collect(Collectors.groupingBy((AuthorDto a) -> a.getName().substring(0, 1).toUpperCase()));
    }
}
