package com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.review;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.user.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_review_like")
public class BookReviewLikeEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY) TODO: REMOVE COMMENT LATER
    private Integer id;

    @Column(name = "review_id", columnDefinition = "INT NOT NULL")
    private Integer reviewId;

    @ManyToOne
    @JoinColumn(name = "user_id", columnDefinition = "INT NOT NULL")
    private UserEntity userEntityLiked;

    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime time;

    @Column(columnDefinition = "SMALLINT NOT NULL")
    private Short value;

    public BookReviewLikeEntity() {}

    public BookReviewLikeEntity(Integer id, Integer reviewId, UserEntity userEntityLiked, LocalDateTime time, Short value) {
        this.id = id;
        this.reviewId = reviewId;
        this.userEntityLiked = userEntityLiked;
        this.time = time;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public UserEntity getUserEntityLiked() {
        return userEntityLiked;
    }

    public void setUserEntityLiked(UserEntity userEntityLikes) {
        this.userEntityLiked = userEntityLikes;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Short getValue() {
        return value;
    }

    public void setValue(Short value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BookReviewLikeEntity{" +
                "id=" + id +
                ", reviewId=" + reviewId +
                ", userEntityLiked=" + userEntityLiked +
                ", time=" + time +
                ", value=" + value +
                '}';
    }
}
