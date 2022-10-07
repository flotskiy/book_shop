package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.CountedBooksDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class RecentPageController extends HeaderController {

    private final BookService bookService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Autowired
    public RecentPageController(UserRegistrationService userRegistrationService, BookService bookService) {
        super(userRegistrationService);
        this.bookService = bookService;
    }

    @ModelAttribute("recentBooksPage")
    public List<BookDto> recentBooks() {
        return bookService.getRecentBooks(0, 20);
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
        from = bookService.checkFrom(from);
        to = bookService.checkTo(to);
        return new CountedBooksDto(bookService
                .getPageOfRecentBooks(LocalDate.parse(from, formatter), LocalDate.parse(to, formatter), offset, limit)
                .getContent()
        );
    }
}
