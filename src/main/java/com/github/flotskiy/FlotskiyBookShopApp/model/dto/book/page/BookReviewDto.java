package com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.page;

public class BookReviewDto {

    private Integer id;
    private String userName;
    private String time;
    private String text;
    private String textHide;
    private Integer likeCount = 0;
    private Integer dislikeCount = 0;
    private Integer rating = 0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextHide() {
        return textHide;
    }

    public void setTextHide(String textHide) {
        this.textHide = textHide;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(Integer dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
