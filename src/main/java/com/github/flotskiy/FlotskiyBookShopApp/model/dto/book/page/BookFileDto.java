package com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.enums.BookFileType;

public class BookFileDto {

    private String hash;
    private Integer typeId;
    private String path;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBookFileExtensionString(){
        return BookFileType.getExtensionStringByTypeId(typeId);
    }
}
