package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.exceptions.RateBookByUserException;
import com.github.flotskiy.FlotskiyBookShopApp.exceptions.RateBookReviewException;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.page.DetailedRatingDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.PopularityDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.review.BookRatingEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.review.BookReviewEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.review.BookReviewLikeEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BooksRatingAndPopularityService {

    private final Book2UserRepository book2UserRepository;
    private final BookRatingRepository bookRatingRepository;
    private final BookRepository bookRepository;
    private final BookReviewRepository bookReviewRepository;
    private final BookReviewLikeRepository bookReviewLikeRepository;
    private final UserRepository userRepository;

    @Autowired
    public BooksRatingAndPopularityService(
            Book2UserRepository book2UserRepository,
            BookRatingRepository bookRatingRepository,
            BookRepository bookRepository,
            BookReviewRepository bookReviewRepository,
            BookReviewLikeRepository bookReviewLikeRepository,
            UserRepository userRepository
            ) {
        this.book2UserRepository = book2UserRepository;
        this.bookRatingRepository = bookRatingRepository;
        this.bookRepository = bookRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.bookReviewLikeRepository = bookReviewLikeRepository;
        this.userRepository = userRepository;
    }

    public Integer calculateBookRating(Integer bookId) {
        List<BookRatingEntity> bookRatingEntityList = bookRatingRepository.findBookRatingEntitiesByBookId(bookId);
        if (bookRatingEntityList.isEmpty()) {
            return 0;
        }
        int allRatingsSum = bookRatingEntityList.stream().mapToInt(BookRatingEntity::getRating).sum();
        int ratingCount = bookRatingEntityList.size();
        double bookRating = ((double) allRatingsSum) / ratingCount;
        return Math.toIntExact(Math.round(bookRating));
    }

    public DetailedRatingDto getDetailedRatingDto(Integer bookId) {
        List<BookRatingEntity> bookRatingEntityList = bookRatingRepository.findBookRatingEntitiesByBookId(bookId);
        Map<Short, Long> ratingsMapCount = bookRatingEntityList.stream()
                .collect(Collectors.groupingBy(BookRatingEntity::getRating, Collectors.counting()));

        DetailedRatingDto detailedRatingDto = new DetailedRatingDto();
        if (bookRatingEntityList.size() == 0) {
            return detailedRatingDto;
        }
        detailedRatingDto.setCount(bookRatingEntityList.size());

        for (Map.Entry<Short, Long> entry : ratingsMapCount.entrySet()) {
            short key = entry.getKey();
            switch (key) {
                case 1:
                    detailedRatingDto.setOneStarCount(Math.toIntExact(ratingsMapCount.get(key)));
                    break;
                case 2:
                    detailedRatingDto.setTwoStarsCount(Math.toIntExact(ratingsMapCount.get(key)));
                    break;
                case 3:
                    detailedRatingDto.setThreeStarsCount(Math.toIntExact(ratingsMapCount.get(key)));
                    break;
                case 4:
                    detailedRatingDto.setFourStarsCount(Math.toIntExact(ratingsMapCount.get(key)));
                    break;
                case 5:
                    detailedRatingDto.setFiveStarsCount(Math.toIntExact(ratingsMapCount.get(key)));
                    break;
            }
        }
        return detailedRatingDto;
    }

    public List<PopularityDto> getAllPopularBooks() {
        List<PopularityDto> result = new ArrayList<>();
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
            double cartRatio = 0.7;
            double keptRatio = 0.4;
            double tempPopularity = paidCount + cartRatio * cartCount + keptRatio * keptCount;
            short popularity = (short) Math.ceil(tempPopularity);
            result.add(new PopularityDto(entry.getKey(), popularity));
        }
        result.sort(Comparator.comparing(PopularityDto::getPopularity).reversed());
        return result;
    }

    public List<Integer> getPopularBookIds(List<PopularityDto> popularityDtoList, Integer offset, Integer limit) {
        int firstElement = offset * limit;
        int lastElement = offset * limit + limit;
        if (popularityDtoList.size() < lastElement) {
            lastElement = popularityDtoList.size();
        }
        if (firstElement > popularityDtoList.size() || firstElement >= lastElement) {
            return Collections.EMPTY_LIST;
        }
        return popularityDtoList.stream().map(PopularityDto::getBookId).collect(Collectors.toList())
                .subList(firstElement, lastElement);
    }

    public void setRatingToBookByUser(Integer bookId, Integer userId, Integer ratingValue)
            throws RateBookByUserException {
        if (ratingValue < 1 || ratingValue > 5) {
            throw new RateBookByUserException("Rating value is unacceptable");
        }
        Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
        if (bookEntity.isEmpty()) {
            throw new RateBookByUserException("Book to rate not found");
        }
        BookRatingEntity bookRatingEntity = bookRatingRepository.findBookRatingEntityByBookIdAndUserId(bookId, userId);
        if (bookRatingEntity == null) {
            Integer newId = bookRatingRepository.getMaxId() + 1;
            BookRatingEntity newBookRatingEntity = new BookRatingEntity(newId, bookId, userId, ratingValue.shortValue());
            bookRatingRepository.save(newBookRatingEntity);
        } else {
            bookRatingRepository.delete(bookRatingEntity);
            bookRatingEntity.setRating(ratingValue.shortValue());
            bookRatingRepository.save(bookRatingEntity);
        }
    }

    public void rateBookReview(Integer reviewId, Integer userId, Integer likeValue) throws RateBookReviewException {
        if (likeValue != 1 && likeValue != -1) {
            throw new RateBookReviewException("Like value is unacceptable");
        }
        Optional<BookReviewEntity> bookReviewEntity = bookReviewRepository.findById(reviewId);
        if (bookReviewEntity.isEmpty()) {
            throw new RateBookReviewException("Review to rate not found");
        }
        Optional<BookEntity> bookEntity = bookRepository.findById(bookReviewEntity.get().getBookId());
        if (bookEntity.isEmpty()) {
            throw new RateBookReviewException("Book that owns the review to rate not found");
        }
        BookReviewLikeEntity bookReviewLikeEntity =
                bookReviewLikeRepository.findBookReviewLikeEntityByReviewIdAndUserId(reviewId, userId);
        if (bookReviewLikeEntity == null) {
            Integer newId = bookReviewLikeRepository.getMaxId() + 1;
            UserEntity userEntity = userRepository.findById(userId).get();
            BookReviewLikeEntity newBookReviewLikeEntity =
                    new BookReviewLikeEntity(newId, reviewId, userEntity, LocalDateTime.now(), likeValue.shortValue());
            System.out.println(newBookReviewLikeEntity);
            bookReviewLikeRepository.save(newBookReviewLikeEntity);
        } else {
            bookReviewLikeRepository.delete(bookReviewLikeEntity);
            bookReviewLikeEntity.setValue(likeValue.shortValue());
            bookReviewLikeEntity.setTime(LocalDateTime.now());
            bookReviewLikeRepository.save(bookReviewLikeEntity);
        }
    }
}
