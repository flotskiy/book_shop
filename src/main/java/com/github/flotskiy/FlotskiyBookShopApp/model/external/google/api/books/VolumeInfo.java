package com.github.flotskiy.FlotskiyBookShopApp.model.external.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

// class created automatically from json here: https://json2csharp.com/code-converters/json-to-pojo
public class VolumeInfo {
    @JsonProperty("title")
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String title;

    @JsonProperty("authors")
    public ArrayList<String> getAuthors() {
        return this.authors;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }

    ArrayList<String> authors;

    @JsonProperty("publisher")
    public String getPublisher() {
        return this.publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    String publisher;

    @JsonProperty("publishedDate")
    public String getPublishedDate() {
        return this.publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    String publishedDate;

    @JsonProperty("description")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    String description;

    @JsonProperty("industryIdentifiers")
    public ArrayList<IndustryIdentifier> getIndustryIdentifiers() {
        return this.industryIdentifiers;
    }

    public void setIndustryIdentifiers(ArrayList<IndustryIdentifier> industryIdentifiers) {
        this.industryIdentifiers = industryIdentifiers;
    }

    ArrayList<IndustryIdentifier> industryIdentifiers;

    @JsonProperty("readingModes")
    public ReadingModes getReadingModes() {
        return this.readingModes;
    }

    public void setReadingModes(ReadingModes readingModes) {
        this.readingModes = readingModes;
    }

    ReadingModes readingModes;

    @JsonProperty("pageCount")
    public int getPageCount() {
        return this.pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    int pageCount;

    @JsonProperty("printType")
    public String getPrintType() {
        return this.printType;
    }

    public void setPrintType(String printType) {
        this.printType = printType;
    }

    String printType;

    @JsonProperty("categories")
    public ArrayList<String> getCategories() {
        return this.categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    ArrayList<String> categories;

    @JsonProperty("maturityRating")
    public String getMaturityRating() {
        return this.maturityRating;
    }

    public void setMaturityRating(String maturityRating) {
        this.maturityRating = maturityRating;
    }

    String maturityRating;

    @JsonProperty("allowAnonLogging")
    public boolean getAllowAnonLogging() {
        return this.allowAnonLogging;
    }

    public void setAllowAnonLogging(boolean allowAnonLogging) {
        this.allowAnonLogging = allowAnonLogging;
    }

    boolean allowAnonLogging;

    @JsonProperty("contentVersion")
    public String getContentVersion() {
        return this.contentVersion;
    }

    public void setContentVersion(String contentVersion) {
        this.contentVersion = contentVersion;
    }

    String contentVersion;

    @JsonProperty("panelizationSummary")
    public PanelizationSummary getPanelizationSummary() {
        return this.panelizationSummary;
    }

    public void setPanelizationSummary(PanelizationSummary panelizationSummary) {
        this.panelizationSummary = panelizationSummary;
    }

    PanelizationSummary panelizationSummary;

    @JsonProperty("imageLinks")
    public ImageLinks getImageLinks() {
        return this.imageLinks;
    }

    public void setImageLinks(ImageLinks imageLinks) {
        this.imageLinks = imageLinks;
    }

    ImageLinks imageLinks;

    @JsonProperty("language")
    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    String language;

    @JsonProperty("previewLink")
    public String getPreviewLink() {
        return this.previewLink;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }

    String previewLink;

    @JsonProperty("infoLink")
    public String getInfoLink() {
        return this.infoLink;
    }

    public void setInfoLink(String infoLink) {
        this.infoLink = infoLink;
    }

    String infoLink;

    @JsonProperty("canonicalVolumeLink")
    public String getCanonicalVolumeLink() {
        return this.canonicalVolumeLink;
    }

    public void setCanonicalVolumeLink(String canonicalVolumeLink) {
        this.canonicalVolumeLink = canonicalVolumeLink;
    }

    String canonicalVolumeLink;
}
