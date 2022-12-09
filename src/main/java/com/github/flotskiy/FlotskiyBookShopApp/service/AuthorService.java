package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.author.AuthorEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.AuthorDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2AuthorEntity;
import com.github.flotskiy.FlotskiyBookShopApp.repository.AuthorRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.Book2AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final Book2AuthorRepository book2AuthorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, Book2AuthorRepository book2AuthorRepository) {
        this.authorRepository = authorRepository;
        this.book2AuthorRepository = book2AuthorRepository;
    }

    public List<AuthorDto> getAuthorsMap() {
        List<AuthorEntity> authorList = authorRepository.findAll();
        List<AuthorDto> authorDtoList = new ArrayList<>();
        for (AuthorEntity author : authorList) {
            AuthorDto authorDto = convertAuthorEntityToAuthorDtoShort(author);
            authorDtoList.add(authorDto);
        }
        return authorDtoList;
    }

    public Map<String, List<AuthorDto>> getAuthorsGroupedMap() {
        List<AuthorDto> authorDtoList = getAuthorsMap();
        Map<String, List<AuthorDto>> result =
                authorDtoList.stream()
                        .peek(authorDto -> {
                            String[] nameArray = authorDto.getName().split(" ", 2);
                            authorDto.setName(nameArray[1] + " " + nameArray[0]);
                        })
                        .collect(Collectors.groupingBy((AuthorDto a) -> a.getName().substring(0, 1).toUpperCase()));
        result.values().forEach(list -> list.sort(Comparator.comparing(AuthorDto::getName)));
        return new TreeMap<>(result);
    }

    public List<AuthorEntity> getAuthorsEntity() {
        return authorRepository.findAll();
    }

    public AuthorDto getAuthorBySlug(String authorSlug) {
        AuthorEntity authorEntity = authorRepository.findAuthorEntityBySlug(authorSlug);
        return convertAuthorEntityToAuthorDto(authorEntity);
    }

    public AuthorDto convertAuthorEntityToAuthorDtoShort(AuthorEntity authorEntity) {
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(authorEntity.getId());
        authorDto.setName(authorEntity.getName());
        authorDto.setSlug(authorEntity.getSlug());
        return authorDto;
    }

    public List<AuthorEntity> sortAuthorEntitiesBySortIndex(Set<AuthorEntity> authorEntities, int bookId) {
        List<AuthorEntity> result = new ArrayList<>();
        List<Book2AuthorEntity> book2AuthorEntityList =
                book2AuthorRepository.findBook2AuthorEntitiesByBookIdOrderBySortIndexAsc(bookId);
        for (Book2AuthorEntity book2Author : book2AuthorEntityList) {
            AuthorEntity author = authorEntities.stream()
                    .filter(authorEntity -> authorEntity.getId() == book2Author.getAuthorId())
                    .findFirst().get();
            result.add(author);
        }
        return result;
    }

    private AuthorDto convertAuthorEntityToAuthorDto(AuthorEntity authorEntity) {
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(authorEntity.getId());
        authorDto.setName(authorEntity.getName());
        authorDto.setPhoto(authorEntity.getPhoto());
        authorDto.setSlug(authorEntity.getSlug());
        setDescription(authorEntity, authorDto);
        authorDto.setBooksCount(authorEntity.getBooks().size());
        return authorDto;
    }

    private void setDescription(AuthorEntity authorEntity, AuthorDto authorDto) {
        int shortLimit = 400;
        String entityDescription = authorEntity.getDescription();
        if (entityDescription.length() <= shortLimit) {
            authorDto.setDescriptionShort(entityDescription);
            authorDto.setDescriptionRemains("");
        } else {
            authorDto.setDescriptionShort(entityDescription.substring(0, shortLimit));
            authorDto.setDescriptionRemains(entityDescription.substring(shortLimit));
        }
    }
}
