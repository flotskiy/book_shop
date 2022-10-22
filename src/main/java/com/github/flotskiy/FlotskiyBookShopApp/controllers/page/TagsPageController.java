package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.CountedBooksDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookTagEntity;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.service.TagService;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TagsPageController extends HeaderController {

    private final TagService tagService;

    @Autowired
    public TagsPageController(
            UserRegistrationService userRegistrationService, BookService bookService, TagService tagService
    ) {
        super(userRegistrationService, bookService);
        this.tagService = tagService;
    }

    @GetMapping("/tags/{tagSlug}")
    public String tagPage(@PathVariable("tagSlug") String tagSlug, Model model) {
        BookTagEntity tagEntity = tagService.getBookTagBySlug(tagSlug);
        List<BookDto> bookDtos = getBookService().getPageOfBooksByTag(tagEntity.getId(), 0, 20).getContent();
        model.addAttribute("tagBookList", bookDtos);
        model.addAttribute("tagObject", tagEntity);
        return "/tags/index";
    }

    @GetMapping("/books/tag/{tagId}")
    @ResponseBody
    public CountedBooksDto getNextTagPage(
            @PathVariable("tagId") Integer tagId,
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit
    ) {
        return new CountedBooksDto(getBookService().getPageOfBooksByTag(tagId, offset, limit).getContent());
    }
}
