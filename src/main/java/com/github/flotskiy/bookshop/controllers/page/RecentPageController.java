package com.github.flotskiy.bookshop.controllers.page;

import com.github.flotskiy.bookshop.model.dto.book.BookDto;
import com.github.flotskiy.bookshop.model.dto.book.CountedBooksDto;
import com.github.flotskiy.bookshop.model.dto.user.UserDto;
import com.github.flotskiy.bookshop.service.BookService;
import com.github.flotskiy.bookshop.security.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@PropertySource("application-variables.properties")
public class RecentPageController extends HeaderController {

    @Value("${initial.offset}")
    private int offset;

    @Value("${page.limit}")
    private int limit;


    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Autowired
    public RecentPageController(UserRegistrationService userRegistrationService, BookService bookService) {
        super(userRegistrationService, bookService);
    }

    @ModelAttribute("recentBooksPage")
    public List<BookDto> recentBooks() {
        UserDto userDto = getUserRegistrationService().getCurrentUserDto();
        return getBookService().getRecentBooks(offset, limit, userDto);
    }

    @GetMapping("/books/recent")
    public String recentPage() {
        return "/books/recent";
    }

    @GetMapping("/books/recent/more")
    @ResponseBody
    public CountedBooksDto getNextRecentPage(@RequestParam(value = "from", required = false) String from,
                                             @RequestParam(value = "to", required = false) String to,
                                             @RequestParam(value = "offset", required = false) Integer offset,
                                             @RequestParam(value = "limit", required = false) Integer limit) {
        from = getBookService().checkFrom(from);
        to = getBookService().checkTo(to);
        UserDto userDto = getUserRegistrationService().getCurrentUserDto();
        return new CountedBooksDto(getBookService()
                .getPageOfRecentBooks(
                        LocalDate.parse(from, formatter), LocalDate.parse(to, formatter), offset, limit, userDto
                )
                .getContent());
    }
}
