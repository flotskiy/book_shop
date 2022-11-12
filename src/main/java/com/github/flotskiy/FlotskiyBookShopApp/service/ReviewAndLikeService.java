package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.aspect.annotations.EntityCreationControllable;
import com.github.flotskiy.FlotskiyBookShopApp.exceptions.BookReviewException;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.page.BookReviewDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.review.BookReviewEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.review.BookReviewLikeEntity;
import com.github.flotskiy.FlotskiyBookShopApp.repository.BookRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.BookReviewLikeRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.BookReviewRepository;
import com.github.flotskiy.FlotskiyBookShopApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewAndLikeService {

    private final DateTimeFormatter formatter;
    private final BookReviewRepository bookReviewRepository;
    private final UserRepository userRepository;
    private final BookReviewLikeRepository bookReviewLikeRepository;
    private final BookRepository bookRepository;

    @Autowired
    public ReviewAndLikeService(
            BookReviewRepository bookReviewRepository,
            UserRepository userRepository,
            BookReviewLikeRepository bookReviewLikeRepository,
            BookRepository bookRepository
    ) {
        this.bookReviewRepository = bookReviewRepository;
        this.userRepository = userRepository;
        this.bookReviewLikeRepository = bookReviewLikeRepository;
        this.bookRepository = bookRepository;
        this.formatter = getFormatter();
    }

    public List<BookReviewDto> getBookReviewDtos(Integer bookId) {
        List<BookReviewEntity> bookReviewEntities = bookReviewRepository.findBookReviewEntitiesByBookId(bookId);
        List<BookReviewDto> result = convertBookReviewEntitiesListToBookReviewDtoList(bookReviewEntities);
        result.sort((o1, o2) -> {
            LocalDateTime t1 = LocalDateTime.parse(o1.getTime(), formatter);
            LocalDateTime t2 = LocalDateTime.parse(o2.getTime(), formatter);
            return t1.compareTo(t2);
        });
        return result;
    }

    @EntityCreationControllable
    public BookReviewEntity bookReview(Integer bookId, Integer userId, String text) {
        if (text.length() < 15) {
            throw new BookReviewException("Review is to short");
        }
        Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
        if (bookEntity.isEmpty()) {
            throw new BookReviewException("Book to review not found");
        }
        BookReviewEntity bookReviewEntity = bookReviewRepository.findBookReviewEntityByBookIdAndUserId(bookId, userId);
        if (bookReviewEntity == null) {
            BookReviewEntity newBookReviewEntity = new BookReviewEntity();
            Integer newId = bookReviewRepository.getMaxId() + 1;
            newBookReviewEntity.setId(newId);
            newBookReviewEntity.setBookId(bookId);
            newBookReviewEntity.setUserId(userId);
            newBookReviewEntity.setTime(LocalDateTime.now());
            newBookReviewEntity.setText(text);
            return bookReviewRepository.save(newBookReviewEntity);
        } else {
            bookReviewRepository.delete(bookReviewEntity);
            bookReviewEntity.setTime(LocalDateTime.now());
            bookReviewEntity.setText(text);
            return bookReviewRepository.save(bookReviewEntity);
        }
    }

    public BookReviewDto convertBookReviewEntityToBookReviewDto(BookReviewEntity bookReviewEntity) {
        BookReviewDto bookReviewDto = new BookReviewDto();
        bookReviewDto.setId(bookReviewEntity.getId());
        String userName = userRepository.findById(bookReviewEntity.getUserId()).get().getName();
        bookReviewDto.setUserName(userName);
        String dateString = bookReviewEntity.getTime().format(formatter);
        bookReviewDto.setTime(dateString);
        int textHideLimit = 400;
        if (bookReviewEntity.getText().length() >= textHideLimit) {
            bookReviewDto.setText(bookReviewEntity.getText().substring(0, textHideLimit));
            bookReviewDto.setTextHide(bookReviewEntity.getText().substring(textHideLimit));
        } else {
            bookReviewDto.setText(bookReviewEntity.getText());
        }

        List<BookReviewLikeEntity> bookReviewLikeEntityList =
                bookReviewLikeRepository.findBookReviewLikeEntitiesByReviewId(bookReviewEntity.getId());
        Map<Short, Long> likesCount = bookReviewLikeEntityList.stream()
                .collect(Collectors.groupingBy(BookReviewLikeEntity::getValue, Collectors.counting()));
        int likes = 0;
        int dislikes = 0;
        for (Map.Entry<Short, Long> entry : likesCount.entrySet()) {
            short key = entry.getKey();
            switch (key) {
                case 1:
                    likes = Math.toIntExact(likesCount.get(key));
                    bookReviewDto.setLikeCount(likes);
                    break;
                case -1:
                    dislikes = Math.toIntExact(likesCount.get(key));
                    bookReviewDto.setDislikeCount(dislikes);
                    break;
            }
        }
        bookReviewDto.setRating(calculateReviewRating(likes, dislikes));
        return bookReviewDto;
    }

    public int calculateReviewRating(int likes, int dislikes) {
        int secondStarLimit = 20;
        int thirdStarLimit = 40;
        int fourthStarLimit = 60;
        int fifthStarLimit = 80;

        int total = likes + dislikes;
        if (total == 0) {
            return 0;
        }

        double totalPerCent = 100.;
        int likesPerCent = (int) ((totalPerCent * likes) / total);

        if (likesPerCent >= fifthStarLimit) {
            return 5;
        } else if (likesPerCent >= fourthStarLimit) {
            return 4;
        } else if (likesPerCent >= thirdStarLimit) {
            return 3;
        } else if (likesPerCent >= secondStarLimit) {
            return 2;
        }
        return 1;
    }

    private List<BookReviewDto> convertBookReviewEntitiesListToBookReviewDtoList(List<BookReviewEntity> bookReviewEntities) {
        return bookReviewEntities.stream().map(this::convertBookReviewEntityToBookReviewDto).collect(Collectors.toList());
    }

    private DateTimeFormatter getFormatter() {
        return new DateTimeFormatterBuilder().appendPattern("dd.MM.yyyy[ HH:mm]")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter();
    }
}
