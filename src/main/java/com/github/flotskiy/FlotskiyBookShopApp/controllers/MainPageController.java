package com.github.flotskiy.FlotskiyBookShopApp.controllers;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

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
        return bookService.getBooksData().subList(0, 20);
    }

    @ModelAttribute("recentBooks")
    public List<BookDto> recentBooks() {
        return bookService.getBooksData().subList(0, 20);
    }

    @ModelAttribute("popularBooks")
    public List<BookDto> popularBooks() {
        return bookService.getBooksData().subList(0, 20);
    }

    @GetMapping({"/"})
    public String mainPage() {
        return "/index";
    }

    @GetMapping("/postponed")
    public String postponedPage() {
        return "/postponed";
    }
}
