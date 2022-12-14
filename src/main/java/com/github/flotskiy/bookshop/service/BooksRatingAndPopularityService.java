package com.github.flotskiy.bookshop.service;

import com.github.flotskiy.bookshop.aspect.annotations.EntityCreationControllable;
import com.github.flotskiy.bookshop.exceptions.RateBookByUserException;
import com.github.flotskiy.bookshop.exceptions.RateBookReviewException;
import com.github.flotskiy.bookshop.model.dto.book.BookDto;
import com.github.flotskiy.bookshop.model.dto.book.page.DetailedRatingDto;
import com.github.flotskiy.bookshop.model.dto.book.PopularityDto;
import com.github.flotskiy.bookshop.model.dto.user.UserDto;
import com.github.flotskiy.bookshop.model.entity.book.BookEntity;
import com.github.flotskiy.bookshop.model.entity.book.links.Book2UserEntity;
import com.github.flotskiy.bookshop.model.entity.book.review.BookRatingEntity;
import com.github.flotskiy.bookshop.model.entity.book.review.BookReviewEntity;
import com.github.flotskiy.bookshop.model.entity.book.review.BookReviewLikeEntity;
import com.github.flotskiy.bookshop.model.entity.user.UserEntity;
import com.github.flotskiy.bookshop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
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
        if (bookRatingEntityList.isEmpty()) {
            return detailedRatingDto;
        }
        detailedRatingDto.setCount(bookRatingEntityList.size());

        for (Map.Entry<Short, Long> entry : ratingsMapCount.entrySet()) {
            switch (entry.getKey()) {
                case 1:
                    detailedRatingDto.setOneStarCount(Math.toIntExact(entry.getValue()));
                    break;
                case 2:
                    detailedRatingDto.setTwoStarsCount(Math.toIntExact(entry.getValue()));
                    break;
                case 3:
                    detailedRatingDto.setThreeStarsCount(Math.toIntExact(entry.getValue()));
                    break;
                case 4:
                    detailedRatingDto.setFourStarsCount(Math.toIntExact(entry.getValue()));
                    break;
                case 5:
                    detailedRatingDto.setFiveStarsCount(Math.toIntExact(entry.getValue()));
                    break;
                default:
                    throw new NoSuchElementException("Wrong rating value");
            }
        }
        return detailedRatingDto;
    }

    public List<PopularityDto> getAllPopularBooks(UserDto userDto) {
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
        List<Integer> allUserBooksId =
                userDto.getUserBooksData().getAllBooks().stream().map(BookDto::getId).collect(Collectors.toList());
        return result.stream()
                .filter(popularityDto -> !allUserBooksId.contains(popularityDto.getBookId()))
                .sorted(Comparator.comparing(PopularityDto::getPopularity).reversed()).collect(Collectors.toList());
    }

    public List<Integer> getPopularBookIds(List<PopularityDto> popularityDtoList, Integer offset, Integer limit) {
        int firstElement = offset * limit;
        int lastElement = offset * limit + limit;
        if (popularityDtoList.size() < lastElement) {
            lastElement = popularityDtoList.size();
        }
        if (firstElement > popularityDtoList.size() || firstElement >= lastElement) {
            return Collections.emptyList();
        }
        return popularityDtoList.stream().map(PopularityDto::getBookId).collect(Collectors.toList())
                .subList(firstElement, lastElement);
    }

    @EntityCreationControllable
    public BookRatingEntity setRatingToBookByUser(Integer bookId, Integer userId, Integer ratingValue) {
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
            return bookRatingRepository.save(newBookRatingEntity);
        } else {
            bookRatingRepository.delete(bookRatingEntity);
            bookRatingEntity.setRating(ratingValue.shortValue());
            return bookRatingRepository.save(bookRatingEntity);
        }
    }

    @EntityCreationControllable
    public BookReviewLikeEntity rateBookReview(Integer reviewId, Integer userId, Integer likeValue) {
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
            Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
            UserEntity userEntity = userEntityOptional.orElseThrow(EntityNotFoundException::new);
            BookReviewLikeEntity newBookReviewLikeEntity =
                    new BookReviewLikeEntity(newId, reviewId, userEntity, LocalDateTime.now(), likeValue.shortValue());
            return bookReviewLikeRepository.save(newBookReviewLikeEntity);
        } else {
            bookReviewLikeRepository.delete(bookReviewLikeEntity);
            bookReviewLikeEntity.setValue(likeValue.shortValue());
            bookReviewLikeEntity.setTime(LocalDateTime.now());
            return bookReviewLikeRepository.save(bookReviewLikeEntity);
        }
    }

    public List<Integer> getFirst30bookIdsWithMaxUsersRatingMoreOrEquals4() {
        return bookRatingRepository.getFirst30bookIdsWithMaxUsersRatingMoreOrEquals4();
    }

    public boolean isRateBookPossible(Integer bookId, Integer userId) {
        BookRatingEntity bookRatingEntity = bookRatingRepository.findBookRatingEntityByBookIdAndUserId(bookId, userId);
        return bookRatingEntity == null;
    }
}
