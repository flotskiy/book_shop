package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.CountedBooksDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.SearchWordDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class RecentPageController {

    private final BookService bookService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Autowired
    public RecentPageController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("recentBooksPage")
    public List<BookDto> recentBooks() {
        return bookService.getRecentBooks(0, 20);
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @GetMapping("/recent")
    public String recentPage() {
        return "/books/recent";
    }

    @GetMapping("/books/recent")
    @ResponseBody
    public CountedBooksDto getNextRecentPage(@RequestParam(value = "from", required = false) String from,
                                             @RequestParam(value = "to", required = false) String to,
                                             @RequestParam(value = "offset", required = false) Integer offset,
                                             @RequestParam(value = "limit", required = false) Integer limit) {
        if (from.isEmpty()) {
            from = "01.01.1900";
        }
        if (to.isEmpty()) {
            to = LocalDate.now().format(formatter);
        }
        return new CountedBooksDto(bookService
                .getPageOfRecentBooks(LocalDate.parse(from, formatter), LocalDate.parse(to, formatter), offset, limit)
                .getContent()
        );
    }
}
