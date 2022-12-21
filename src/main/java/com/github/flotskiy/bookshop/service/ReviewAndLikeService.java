package com.github.flotskiy.bookshop.service;

import com.github.flotskiy.bookshop.aspect.annotations.EntityCreationControllable;
import com.github.flotskiy.bookshop.exceptions.BookReviewException;
import com.github.flotskiy.bookshop.model.dto.book.page.BookReviewDto;
import com.github.flotskiy.bookshop.model.entity.book.BookEntity;
import com.github.flotskiy.bookshop.model.entity.book.review.BookReviewEntity;
import com.github.flotskiy.bookshop.model.entity.book.review.BookReviewLikeEntity;
import com.github.flotskiy.bookshop.model.entity.user.UserEntity;
import com.github.flotskiy.bookshop.repository.BookRepository;
import com.github.flotskiy.bookshop.repository.BookReviewLikeRepository;
import com.github.flotskiy.bookshop.repository.BookReviewRepository;
import com.github.flotskiy.bookshop.repository.UserRepository;
import com.github.flotskiy.bookshop.util.CustomStringHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewAndLikeService {

    private final BookReviewRepository bookReviewRepository;
    private final UserRepository userRepository;
    private final BookReviewLikeRepository bookReviewLikeRepository;
    private final BookRepository bookRepository;
    private final CustomStringHandler customStringHandler;

    @Autowired
    public ReviewAndLikeService(
            BookReviewRepository bookReviewRepository,
            UserRepository userRepository,
            BookReviewLikeRepository bookReviewLikeRepository,
            BookRepository bookRepository,
            CustomStringHandler customStringHandler
    ) {
        this.bookReviewRepository = bookReviewRepository;
        this.userRepository = userRepository;
        this.bookReviewLikeRepository = bookReviewLikeRepository;
        this.bookRepository = bookRepository;
        this.customStringHandler = customStringHandler;
    }

    public List<BookReviewDto> getBookReviewDtos(Integer bookId) {
        List<BookReviewEntity> bookReviewEntities = bookReviewRepository.findBookReviewEntitiesByBookId(bookId);
        List<BookReviewDto> result = convertBookReviewEntitiesListToBookReviewDtoList(bookReviewEntities);
        result.sort(Comparator.comparing(BookReviewDto::getRating).reversed());
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
        Optional<UserEntity> userEntityOptional = userRepository.findById(bookReviewEntity.getUserId());
        UserEntity userEntity = userEntityOptional.orElseThrow(EntityNotFoundException::new);
        String userName = userEntity.getName();
        bookReviewDto.setUserName(userName);
        String dateString = bookReviewEntity.getTime().format(customStringHandler.getFormatter());
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
            if (key == 1) {
                likes = Math.toIntExact(entry.getValue());
                bookReviewDto.setLikeCount(likes);
            }
            if (key == -1) {
                dislikes = Math.toIntExact(entry.getValue());
                bookReviewDto.setDislikeCount(dislikes);
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
}
