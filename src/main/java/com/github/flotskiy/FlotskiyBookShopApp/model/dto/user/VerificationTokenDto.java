package com.github.flotskiy.FlotskiyBookShopApp.model.dto.user;

public class VerificationTokenDto {

    private Boolean isValid;
    private String name;
    private String contact;
    private String hash;

    public VerificationTokenDto() {
        this.isValid = true;
    }

    public Boolean isValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
