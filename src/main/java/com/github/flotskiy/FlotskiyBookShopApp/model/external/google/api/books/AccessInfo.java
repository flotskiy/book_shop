package com.github.flotskiy.FlotskiyBookShopApp.model.external.google.api.books;

import com.fasterxml.jackson.annotation.JsonProperty;

// class created automatically from json here: https://json2csharp.com/code-converters/json-to-pojo
public class AccessInfo{
    @JsonProperty("country")
    public String getCountry() { 
		 return this.country; } 
    public void setCountry(String country) { 
		 this.country = country; } 
    String country;
    @JsonProperty("viewability") 
    public String getViewability() { 
		 return this.viewability; } 
    public void setViewability(String viewability) { 
		 this.viewability = viewability; } 
    String viewability;
    @JsonProperty("embeddable") 
    public boolean getEmbeddable() { 
		 return this.embeddable; } 
    public void setEmbeddable(boolean embeddable) { 
		 this.embeddable = embeddable; } 
    boolean embeddable;
    @JsonProperty("publicDomain") 
    public boolean getPublicDomain() { 
		 return this.publicDomain; } 
    public void setPublicDomain(boolean publicDomain) { 
		 this.publicDomain = publicDomain; } 
    boolean publicDomain;
    @JsonProperty("textToSpeechPermission") 
    public String getTextToSpeechPermission() { 
		 return this.textToSpeechPermission; } 
    public void setTextToSpeechPermission(String textToSpeechPermission) { 
		 this.textToSpeechPermission = textToSpeechPermission; } 
    String textToSpeechPermission;
    @JsonProperty("epub") 
    public Epub getEpub() { 
		 return this.epub; } 
    public void setEpub(Epub epub) { 
		 this.epub = epub; } 
    Epub epub;
    @JsonProperty("pdf") 
    public Pdf getPdf() { 
		 return this.pdf; } 
    public void setPdf(Pdf pdf) { 
		 this.pdf = pdf; } 
    Pdf pdf;
    @JsonProperty("webReaderLink") 
    public String getWebReaderLink() { 
		 return this.webReaderLink; } 
    public void setWebReaderLink(String webReaderLink) { 
		 this.webReaderLink = webReaderLink; } 
    String webReaderLink;
    @JsonProperty("accessViewStatus") 
    public String getAccessViewStatus() { 
		 return this.accessViewStatus; } 
    public void setAccessViewStatus(String accessViewStatus) { 
		 this.accessViewStatus = accessViewStatus; } 
    String accessViewStatus;
    @JsonProperty("quoteSharingAllowed") 
    public boolean getQuoteSharingAllowed() { 
		 return this.quoteSharingAllowed; } 
    public void setQuoteSharingAllowed(boolean quoteSharingAllowed) { 
		 this.quoteSharingAllowed = quoteSharingAllowed; } 
    boolean quoteSharingAllowed;
}
