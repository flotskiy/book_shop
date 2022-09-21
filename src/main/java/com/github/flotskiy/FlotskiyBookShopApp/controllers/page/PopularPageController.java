package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.CountedBooksDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PopularPageController extends HeaderController {

    private final BookService bookService;

    @Autowired
    public PopularPageController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("popularBooksPage")
    public List<BookDto> recentBooks() {
        return bookService.getPopularBooks(0, 20);
    }

    @GetMapping("/popular")
    public String popularPage() {
        return "/books/popular";
    }

    @GetMapping("/books/popular")
    @ResponseBody
    public CountedBooksDto getNextPopularPage(@RequestParam(value = "offset", required = false) Integer offset,
                                             @RequestParam(value = "limit", required = false) Integer limit) {
        return new CountedBooksDto(bookService.getPopularBooks(offset, limit));
    }
}
