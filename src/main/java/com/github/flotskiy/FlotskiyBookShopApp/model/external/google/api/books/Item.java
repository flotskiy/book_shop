package com.github.flotskiy.FlotskiyBookShopApp.model.external.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

// class created automatically from json here: https://json2csharp.com/code-converters/json-to-pojo
public class Item {
    @JsonProperty("kind")
    public String getKind() {
        return this.kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    String kind;

    @JsonProperty("id")
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    @JsonProperty("etag")
    public String getEtag() {
        return this.etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    String etag;

    @JsonProperty("selfLink")
    public String getSelfLink() {
        return this.selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    String selfLink;

    @JsonProperty("volumeInfo")
    public VolumeInfo getVolumeInfo() {
        return this.volumeInfo;
    }

    public void setVolumeInfo(VolumeInfo volumeInfo) {
        this.volumeInfo = volumeInfo;
    }

    VolumeInfo volumeInfo;

    @JsonProperty("saleInfo")
    public SaleInfo getSaleInfo() {
        return this.saleInfo;
    }

    public void setSaleInfo(SaleInfo saleInfo) {
        this.saleInfo = saleInfo;
    }

    SaleInfo saleInfo;

    @JsonProperty("accessInfo")
    public AccessInfo getAccessInfo() {
        return this.accessInfo;
    }

    public void setAccessInfo(AccessInfo accessInfo) {
        this.accessInfo = accessInfo;
    }

    AccessInfo accessInfo;

    @JsonProperty("searchInfo")
    public SearchInfo getSearchInfo() {
        return this.searchInfo;
    }

    public void setSearchInfo(SearchInfo searchInfo) {
        this.searchInfo = searchInfo;
    }

    SearchInfo searchInfo;
}
