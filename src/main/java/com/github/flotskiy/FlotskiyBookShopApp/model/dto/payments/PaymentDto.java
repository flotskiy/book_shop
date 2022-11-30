package com.github.flotskiy.FlotskiyBookShopApp.model.dto.payments;

public class PaymentDto {

    private String hash;
    private String sum;
    private Long time;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
