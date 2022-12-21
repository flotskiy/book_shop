package com.github.flotskiy.bookshop.service;

import com.github.flotskiy.bookshop.model.dto.book.GenreDto;
import com.github.flotskiy.bookshop.model.entity.book.links.Book2GenreEntity;
import com.github.flotskiy.bookshop.model.entity.genre.GenreEntity;
import com.github.flotskiy.bookshop.repository.Book2GenreRepository;
import com.github.flotskiy.bookshop.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final Book2GenreRepository book2GenreRepository;

    private List<GenreDto> genreDtoList;

    @Autowired
    public GenreService(GenreRepository genreRepository, Book2GenreRepository book2GenreRepository) {
        this.genreRepository = genreRepository;
        this.book2GenreRepository = book2GenreRepository;
        this.genreDtoList = getAllGenresTree();
    }

    public List<GenreDto> getGenreDtoList() {
        return genreDtoList;
    }

    public List<GenreDto> getAllGenresTree() {
        List<GenreEntity> genreEntities = genreRepository.findAll();
        List<Book2GenreEntity> book2GenreEntities = book2GenreRepository.findAll();
        Map<Integer, GenreDto> allGenreDtoMap = new HashMap<>();
        HashMap<Integer, List<GenreDto>> mapWithGenresDtoLists = new HashMap<>();

        Map<Integer, Long> countedBooksByGenre = book2GenreEntities.stream()
                .collect(Collectors.groupingBy(Book2GenreEntity::getGenreId, Collectors.counting()));

        for(GenreEntity genreEntity : genreEntities) {
            Integer parentId = genreEntity.getParentId();
            mapWithGenresDtoLists.putIfAbsent(parentId, new ArrayList<>());
            GenreDto genreDto = convertGenreEntityToGenreDto(genreEntity, countedBooksByGenre);
            mapWithGenresDtoLists.get(parentId).add(genreDto);
            allGenreDtoMap.put(genreDto.getId(), genreDto);
        }
        mapWithGenresDtoLists
                .forEach((key1, value) -> value.sort(Comparator.comparing(GenreDto::getBooksCount).reversed()));

        List<GenreDto> genresDtoListFinal = mapWithGenresDtoLists.get(null);
        genresDtoListFinal.sort(Comparator.comparing(GenreDto::getBooksCount).reversed());
        for (Map.Entry<Integer, List<GenreDto>> entry : mapWithGenresDtoLists.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            int key = entry.getKey();
            GenreDto genreDto = allGenreDtoMap.get(key);
            genreDto.setChildren(entry.getValue());
        }
        setTwiceInheritedFieldForEachRootGenre(genresDtoListFinal);
        genreDtoList = genresDtoListFinal;
        return genresDtoListFinal;
    }

    public GenreDto findGenre(String slug, List<GenreDto> list) {
        GenreDto result = null;
        for (GenreDto genre : list) {
            if (genre.getSlug().equals(slug)) {
                return genre;
            }
            if (!genre.getChildren().isEmpty()) {
                result = findGenre(slug, genre.getChildren());
            }
            if (result != null) {
                break;
            }
        }
        return result;
    }

    public GenreDto findParentGenre(GenreDto genreDto) {
        if (genreDto == null) {
            return null;
        }
        Integer parentId = genreDto.getParentId();
        if (parentId != null) {
            Optional<GenreEntity> genreEntityOptional = genreRepository.findById(parentId);
            GenreEntity genreEntity = genreEntityOptional.orElseThrow(EntityNotFoundException::new);
            return convertGenreEntityToShortGenreDto(genreEntity);
        }
        return null;
    }

    private GenreDto convertGenreEntityToGenreDto(GenreEntity genreEntity, Map<Integer, Long> countedBooksByGenre) {
        GenreDto genreDto = convertGenreEntityToShortGenreDto(genreEntity);
        genreDto.setBooksCount(countedBooksByGenre.get(genreDto.getId()).intValue());
        return genreDto;
    }

    private GenreDto convertGenreEntityToShortGenreDto(GenreEntity genreEntity) {
        GenreDto genreDto = new GenreDto();
        int genreId = genreEntity.getId();
        genreDto.setId(genreId);
        genreDto.setParentId(genreEntity.getParentId());
        genreDto.setName(genreEntity.getName());
        genreDto.setSlug(genreEntity.getSlug());
        return genreDto;
    }

    private void setTwiceInheritedFieldForEachRootGenre(List<GenreDto> genreDtoList) {
        for (GenreDto rootGenreDto : genreDtoList) {
            if (!rootGenreDto.getChildren().isEmpty()) {
                for (GenreDto level2GenreDto : rootGenreDto.getChildren()) {
                    if (!level2GenreDto.getChildren().isEmpty()) {
                        rootGenreDto.setTwiceInherited(true);
                        break;
                    }
                }
            }
        }
    }
}
