package com.github.flotskiy.bookshop.model.external.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

// class created automatically from json here: https://json2csharp.com/code-converters/json-to-pojo
public class RetailPrice {
    @JsonProperty("amount")
    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    double amount;

    @JsonProperty("currencyCode")
    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    String currencyCode;

    @JsonProperty("amountInMicros")
    public long getAmountInMicros() {
        return this.amountInMicros;
    }

    public void setAmountInMicros(long amountInMicros) {
        this.amountInMicros = amountInMicros;
    }

    long amountInMicros;
}
