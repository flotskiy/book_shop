package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.CountedBooksDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.SearchWordDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainPageController {

    private final BookService bookService;

    @Autowired
    public MainPageController(BookService bookService) {
        this.bookService = bookService;
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
        return bookService.getPageOfPopularBooks(0, 6);
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @ModelAttribute("searchResults")
    public List<BookDto> searchResults() {
        return new ArrayList<>();
    }

    @GetMapping({"/"})
    public String mainPage() {
        return "/index";
    }

    @GetMapping("/postponed")
    public String postponedPage() {
        return "/postponed";
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
        return new CountedBooksDto(bookService.getPageOfPopularBooks(offset, limit));
    }
}
