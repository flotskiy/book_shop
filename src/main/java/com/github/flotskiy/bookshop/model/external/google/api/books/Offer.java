package com.github.flotskiy.bookshop.model.external.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

// class created automatically from json here: https://json2csharp.com/code-converters/json-to-pojo
public class Offer {
    @JsonProperty("finskyOfferType")
    public int getFinskyOfferType() {
        return this.finskyOfferType;
    }

    public void setFinskyOfferType(int finskyOfferType) {
        this.finskyOfferType = finskyOfferType;
    }

    int finskyOfferType;

    @JsonProperty("listPrice")
    public ListPrice getListPrice() {
        return this.listPrice;
    }

    public void setListPrice(ListPrice listPrice) {
        this.listPrice = listPrice;
    }

    ListPrice listPrice;

    @JsonProperty("retailPrice")
    public RetailPrice getRetailPrice() {
        return this.retailPrice;
    }

    public void setRetailPrice(RetailPrice retailPrice) {
        this.retailPrice = retailPrice;
    }

    RetailPrice retailPrice;
}
