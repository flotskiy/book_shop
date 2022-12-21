package com.github.flotskiy.bookshop.model.external.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// class created automatically from json here: https://json2csharp.com/code-converters/json-to-pojo
public class Root {
    @JsonProperty("kind")
    public String getKind() {
        return this.kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    String kind;

    @JsonProperty("totalItems")
    public int getTotalItems() {
        return this.totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    int totalItems;

    @JsonProperty("items")
    public List<Item> getItems() {
        return this.items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    List<Item> items;
}
