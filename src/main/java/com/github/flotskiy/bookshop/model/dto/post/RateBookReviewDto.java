package com.github.flotskiy.bookshop.model.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RateBookReviewDto {

    @JsonProperty(value = "reviewid")
    private Integer reviewId;
    private Integer value;

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
