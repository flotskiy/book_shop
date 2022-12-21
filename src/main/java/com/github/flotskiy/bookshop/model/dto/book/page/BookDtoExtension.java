package com.github.flotskiy.bookshop.model.dto.book.page;

import com.github.flotskiy.bookshop.model.dto.book.AuthorDto;

import java.util.List;

public class BookDtoExtension {

    private List<Integer> bookIdList;
    private List<AuthorDto> authorList;

    public List<Integer> getBookIdList() {
        return bookIdList;
    }

    public void setBookIdList(List<Integer> bookIdList) {
        this.bookIdList = bookIdList;
    }

    public List<AuthorDto> getAuthorList() {
        return authorList;
    }

    public void setAuthorList(List<AuthorDto> authorList) {
        this.authorList = authorList;
    }
}
