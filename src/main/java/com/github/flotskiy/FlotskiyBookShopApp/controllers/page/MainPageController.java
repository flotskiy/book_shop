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

    private final BookService bookService;
    private final TagService tagService;

    @Autowired
    public MainPageController(
            UserRegistrationService userRegistrationService, BookService bookService, TagService tagService
    ) {
        super(userRegistrationService);
        this.bookService = bookService;
        this.tagService = tagService;
    }

    @ModelAttribute("recommendedBooks")
    public List<BookDto> recommendedBooks() {
        return bookService.getPageOfBooks(0, 6).getContent();
    }

    @ModelAttribute("recentBooks")
    public List<BookDto> recentBooks() {
        return bookService.getRecentBooks(0,6);
    }

    @ModelAttribute("popularBooks")
    public List<BookDto> popularBooks() {
        return bookService.getPopularBooks(0, 6);
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
        return new CountedBooksDto(bookService.getPageOfBooks(offset, limit).getContent());
    }

    @GetMapping("/books/card/recent")
    @ResponseBody
    public CountedBooksDto getRecentBooksPage(@RequestParam("offset") int offset, @RequestParam("limit") int limit) {
        return new CountedBooksDto(bookService.getRecentBooks(offset, limit));
    }

    @GetMapping("/books/card/popular")
    @ResponseBody
    public CountedBooksDto getPopularBooksPage(@RequestParam("offset") int offset, @RequestParam("limit") int limit) {
        return new CountedBooksDto(bookService.getPopularBooks(offset, limit));
    }
}
