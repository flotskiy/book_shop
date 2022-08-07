package com.github.flotskiy.FlotskiyBookShopApp.controllers;

import com.github.flotskiy.FlotskiyBookShopApp.data.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    private final BookService bookService;

    @Autowired
    public MainPageController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping({"/bookshop/main", "/"})
    public String mainPage(Model model) {
        model.addAttribute("bookData", bookService.getBooksData());
        model.addAttribute("searchPlaceholder", "new search placeholder");
        return "index";
    }
}
