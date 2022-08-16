package com.github.flotskiy.FlotskiyBookShopApp.controllers;

import com.github.flotskiy.FlotskiyBookShopApp.dto.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/books/recent")
public class RecentPageController {

    private final BookService bookService;

    @Autowired
    public RecentPageController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("recentBooksPage")
    public List<BookDto> recentBooks() {
        return bookService.getBooksData().subList(0, 20);
    }

    @GetMapping
    public String recentPage() {
        return "/books/recent";
    }
}
