package com.github.flotskiy.bookshop.model.external.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

// class created automatically from json here: https://json2csharp.com/code-converters/json-to-pojo
public class Pdf {
    @JsonProperty("isAvailable")
    public boolean getIsAvailable() {
        return this.isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    boolean isAvailable;

    @JsonProperty("acsTokenLink")
    public String getAcsTokenLink() {
        return this.acsTokenLink;
    }

    public void setAcsTokenLink(String acsTokenLink) {
        this.acsTokenLink = acsTokenLink;
    }

    String acsTokenLink;
}
