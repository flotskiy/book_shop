package com.github.flotskiy.bookshop.service;

import com.github.flotskiy.bookshop.model.dto.book.page.TagDto;
import com.github.flotskiy.bookshop.model.entity.book.BookTagEntity;
import com.github.flotskiy.bookshop.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagService {

    private static final int LG_LIMIT = 3;
    private static final int MD_LIMIT = 5;
    private static final int TAG_LIMIT = 7;
    private static final int SM_LIMIT = 10;
    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<TagDto> getTagsList() {
        List<BookTagEntity> tagEntities = tagRepository.findAll();
        List<BookTagEntity> tagEntitiesSorted = new ArrayList<>(tagEntities);
        tagEntitiesSorted.sort(Comparator.comparingInt(t -> t.getBookEntities().size()));
        Collections.reverse(tagEntitiesSorted);
        return tagEntities.stream()
                .map(tagEntity -> convertTagToTagDtoSorted(tagEntity, tagEntitiesSorted)).collect(Collectors.toList());
    }

    public BookTagEntity getBookTagBySlug(String slug) {
        return tagRepository.findTitleBySlug(slug);
    }

    public Set<TagDto> convertTagEntitySetToTagDtoSet(Set<BookTagEntity> tagEntitySet) {
        return tagEntitySet.stream().map(this::convertTagToTagDtoWithoutClazzValue).collect(Collectors.toSet());
    }

    public TagDto convertTagToTagDtoSorted(BookTagEntity tagEntity, List<BookTagEntity> tagEntitiesSorted) {
        TagDto tagDto = convertTagToTagDtoWithoutClazzValue(tagEntity);
        tagDto.setClazz(defineTagClass(tagEntity, tagEntitiesSorted));
        return tagDto;
    }

    private TagDto convertTagToTagDtoWithoutClazzValue(BookTagEntity tagEntity) {
        TagDto tagDto = new TagDto();
        tagDto.setId(tagEntity.getId());
        tagDto.setTitle(tagEntity.getTitle());
        tagDto.setSlug(tagEntity.getSlug());
        return tagDto;
    }

    private String defineTagClass(BookTagEntity tagEntity, List<BookTagEntity> tagEntitiesSorted) {
        int tagIndex = tagEntitiesSorted.indexOf(tagEntity);
        String tagClassName = "Tag";
        if (tagIndex >= 0 && tagIndex < LG_LIMIT) {
            tagClassName += " Tag_lg";
        } else if (tagIndex < (LG_LIMIT + MD_LIMIT)) {
            tagClassName += " Tag_md";
        } else if (tagIndex < (LG_LIMIT + MD_LIMIT + TAG_LIMIT)) {
            return tagClassName;
        } else if (tagIndex < (LG_LIMIT + MD_LIMIT + TAG_LIMIT + SM_LIMIT)) {
            tagClassName += " Tag_sm";
        } else {
            tagClassName += " Tag_xs";
        }
        return tagClassName;
    }
}
