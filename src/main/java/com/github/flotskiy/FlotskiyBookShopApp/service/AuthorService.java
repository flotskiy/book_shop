package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.author.AuthorEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.AuthorDto;
import com.github.flotskiy.FlotskiyBookShopApp.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
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
        return authorDtoList.stream()
                .peek(authorDto -> {
                    String[] nameArray = authorDto.getName().split(" ", 2);
                    authorDto.setName(nameArray[0] + " " + nameArray[1]);
                })
                .collect(Collectors.groupingBy((AuthorDto a) -> a.getName().substring(0, 1).toUpperCase()));
    }

    public List<AuthorEntity> getAuthorsEntity() {
        return authorRepository.findAll();
    }

    public AuthorDto getAuthorBySlug(String authorSlug) {
        AuthorEntity authorEntity = authorRepository.findAuthorEntityBySlug(authorSlug);
        return convertAuthorEntityToAuthorDto(authorEntity);
    }

    private AuthorDto convertAuthorEntityToAuthorDtoShort(AuthorEntity authorEntity) {
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(authorEntity.getId());
        authorDto.setName(authorEntity.getName());
        authorDto.setSlug(authorEntity.getSlug());
        return authorDto;
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
