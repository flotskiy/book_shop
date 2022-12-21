package com.github.flotskiy.bookshop.controllers.page;

import com.github.flotskiy.bookshop.model.dto.book.BookDto;
import com.github.flotskiy.bookshop.model.dto.book.CountedBooksDto;
import com.github.flotskiy.bookshop.model.dto.book.page.TagDto;
import com.github.flotskiy.bookshop.model.dto.user.UserDto;
import com.github.flotskiy.bookshop.service.BookService;
import com.github.flotskiy.bookshop.service.TagService;
import com.github.flotskiy.bookshop.security.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@PropertySource("application-variables.properties")
public class MainPageController extends HeaderController {

    @Value("${initial.offset}")
    private int offset;

    @Value("${slider.card.limit}")
    private int cardLimit;

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
        UserDto userDto = getUserRegistrationService().getCurrentUserDto();
        return  getBookService().getListOfRecommendedBooks(offset, cardLimit, userDto);
    }

    @ModelAttribute("recentBooks")
    public List<BookDto> recentBooks() {
        UserDto userDto = getUserRegistrationService().getCurrentUserDto();
        return getBookService().getRecentBooks(offset, cardLimit, userDto);
    }

    @ModelAttribute("popularBooks")
    public List<BookDto> popularBooks() {
        UserDto userDto = getUserRegistrationService().getCurrentUserDto();
        return getBookService().getPopularBooks(offset, cardLimit, userDto);
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
        UserDto userDto = getUserRegistrationService().getCurrentUserDto();
        return new CountedBooksDto(getBookService().getListOfRecommendedBooks(offset, limit, userDto));
    }

    @GetMapping("/books/card/recent")
    @ResponseBody
    public CountedBooksDto getRecentBooksPage(@RequestParam("offset") int offset, @RequestParam("limit") int limit) {
        UserDto userDto = getUserRegistrationService().getCurrentUserDto();
        return new CountedBooksDto(getBookService().getRecentBooks(offset, limit, userDto));
    }

    @GetMapping("/books/card/popular")
    @ResponseBody
    public CountedBooksDto getPopularBooksPage(@RequestParam("offset") int offset, @RequestParam("limit") int limit) {
        UserDto userDto = getUserRegistrationService().getCurrentUserDto();
        return new CountedBooksDto(getBookService().getPopularBooks(offset, limit, userDto));
    }
}
