package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.RatingDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.repository.Book2UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BooksRatingAndPopularityService {

    private final double cartRatio = 0.7;
    private final double keptRatio = 0.4;

    private final Book2UserRepository book2UserRepository;

    @Autowired
    public BooksRatingAndPopularityService(Book2UserRepository book2UserRepository) {
        this.book2UserRepository = book2UserRepository;
    }

    public List<RatingDto> getAllBooksRating() {
        List<RatingDto> result = new ArrayList<>();
        List<Book2UserEntity> book2UserEntitiesList = book2UserRepository.findAll();
        Map<Integer, List<Book2UserEntity>> mapGroupedByBookIds =
                book2UserEntitiesList.stream().collect(Collectors.groupingBy(Book2UserEntity::getBookId));

        for (Map.Entry<Integer, List<Book2UserEntity>> entry : mapGroupedByBookIds.entrySet()) {
            int paidCount = 0;
            int cartCount = 0;
            int keptCount = 0;
            for (Book2UserEntity book2UserEntity : entry.getValue()) {
                if (book2UserEntity.getTypeId() == 3) {
                    paidCount++;
                } else if (book2UserEntity.getTypeId() == 2) {
                    cartCount++;
                } else if (book2UserEntity.getTypeId() == 1) {
                    keptCount++;
                }
            }
            double tempRating = paidCount + cartRatio * cartCount + keptRatio * keptCount;
            short rating = (short) Math.ceil(tempRating);
            result.add(new RatingDto(entry.getKey(), rating));
        }
        result.sort(Comparator.comparing(RatingDto::getRating).reversed());
        return result;
    }

    public List<Integer> getPopularBookIds(List<RatingDto> ratingDtoList, Integer offset, Integer limit) {
        int firstElement = offset * limit;
        int lastElement = offset * limit + limit;
        if (ratingDtoList.size() < lastElement) {
            lastElement = ratingDtoList.size();
        }
        if (firstElement > ratingDtoList.size() || firstElement >= lastElement) {
            return Collections.EMPTY_LIST;
        }
        return ratingDtoList.stream().map(RatingDto::getBookId).collect(Collectors.toList())
                .subList(firstElement, lastElement);
    }
}
