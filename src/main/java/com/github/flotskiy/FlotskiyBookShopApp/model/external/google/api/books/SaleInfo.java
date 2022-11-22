package com.github.flotskiy.FlotskiyBookShopApp.model.external.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

// class created automatically from json here: https://json2csharp.com/code-converters/json-to-pojo
public class SaleInfo {
    @JsonProperty("country")
    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    String country;

    @JsonProperty("saleability")
    public String getSaleability() {
        return this.saleability;
    }

    public void setSaleability(String saleability) {
        this.saleability = saleability;
    }

    String saleability;

    @JsonProperty("isEbook")
    public boolean getIsEbook() {
        return this.isEbook;
    }

    public void setIsEbook(boolean isEbook) {
        this.isEbook = isEbook;
    }

    boolean isEbook;

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

    @JsonProperty("buyLink")
    public String getBuyLink() {
        return this.buyLink;
    }

    public void setBuyLink(String buyLink) {
        this.buyLink = buyLink;
    }

    String buyLink;

    @JsonProperty("offers")
    public ArrayList<Offer> getOffers() {
        return this.offers;
    }

    public void setOffers(ArrayList<Offer> offers) {
        this.offers = offers;
    }

    ArrayList<Offer> offers;
}
