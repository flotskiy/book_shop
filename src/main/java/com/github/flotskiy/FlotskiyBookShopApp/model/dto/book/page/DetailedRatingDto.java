package com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.page;

public class DetailedRatingDto {

    private Integer count = 0;
    private Integer oneStarCount = 0;
    private Integer twoStarsCount = 0;
    private Integer threeStarsCount = 0;
    private Integer fourStarsCount = 0;
    private Integer fiveStarsCount = 0;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getOneStarCount() {
        return oneStarCount;
    }

    public void setOneStarCount(Integer oneStarCount) {
        this.oneStarCount = oneStarCount;
    }

    public Integer getTwoStarsCount() {
        return twoStarsCount;
    }

    public void setTwoStarsCount(Integer twoStarsCount) {
        this.twoStarsCount = twoStarsCount;
    }

    public Integer getThreeStarsCount() {
        return threeStarsCount;
    }

    public void setThreeStarsCount(Integer threeStarsCount) {
        this.threeStarsCount = threeStarsCount;
    }

    public Integer getFourStarsCount() {
        return fourStarsCount;
    }

    public void setFourStarsCount(Integer fourStarsCount) {
        this.fourStarsCount = fourStarsCount;
    }

    public Integer getFiveStarsCount() {
        return fiveStarsCount;
    }

    public void setFiveStarsCount(Integer fiveStarsCount) {
        this.fiveStarsCount = fiveStarsCount;
    }
}
