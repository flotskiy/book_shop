package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.CountedBooksDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.page.TagDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.service.TagService;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MainPageController extends HeaderController {

    private final TagService tagService;

    @Autowired
    public MainPageController(
            UserRegistrationService userRegistrationService, BookService bookService, TagService tagService
    ) {
        super(userRegistrationService, bookService);
        this.tagService = tagService;
    }

    @ModelAttribute("recommendedBooks")
    public List<BookDto> recommendedBooks() {
        Integer userId = getUserRegistrationService().getCurrentUserId();
        return getBookService().getPageOfBooks(0, 6, userId).getContent();
    }

    @ModelAttribute("recentBooks")
    public List<BookDto> recentBooks() {
        Integer userId = getUserRegistrationService().getCurrentUserId();
        return getBookService().getRecentBooks(0,6, userId);
    }

    @ModelAttribute("popularBooks")
    public List<BookDto> popularBooks() {
        Integer userId = getUserRegistrationService().getCurrentUserId();
        return getBookService().getPopularBooks(0, 6, userId);
    }

    @ModelAttribute("tagsCloud")
    public List<TagDto> tags() {
        return tagService.getTagsList();
    }

    @GetMapping({"/"})
    public String mainPage() {
        return "/index";
    }

    @GetMapping("/books/card/recommended")
    @ResponseBody
    public CountedBooksDto getRecommendedBooksPage(@RequestParam("offset") int offset, @RequestParam("limit") int limit) {
        Integer userId = getUserRegistrationService().getCurrentUserId();
        return new CountedBooksDto(getBookService().getPageOfBooks(offset, limit, userId).getContent());
    }

    @GetMapping("/books/card/recent")
    @ResponseBody
    public CountedBooksDto getRecentBooksPage(@RequestParam("offset") int offset, @RequestParam("limit") int limit) {
        Integer userId = getUserRegistrationService().getCurrentUserId();
        return new CountedBooksDto(getBookService().getRecentBooks(offset, limit, userId));
    }

    @GetMapping("/books/card/popular")
    @ResponseBody
    public CountedBooksDto getPopularBooksPage(@RequestParam("offset") int offset, @RequestParam("limit") int limit) {
        Integer userId = getUserRegistrationService().getCurrentUserId();
        return new CountedBooksDto(getBookService().getPopularBooks(offset, limit, userId));
    }
}
