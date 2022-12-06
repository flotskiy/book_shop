package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.CountedBooksDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@PropertySource("application-variables.properties")
public class PopularPageController extends HeaderController {

    @Value("${initial.offset}")
    private int offset;

    @Value("${page.limit}")
    private int limit;

    @Autowired
    public PopularPageController(UserRegistrationService userRegistrationService, BookService bookService) {
        super(userRegistrationService, bookService);
    }

    @ModelAttribute("popularBooksPage")
    public List<BookDto> recentBooks() {
        UserDto userDto = getUserRegistrationService().getCurrentUserDto();
        return getBookService().getPopularBooks(offset, limit, userDto);
    }

    @GetMapping("/books/popular")
    public String popularPage() {
        return "/books/popular";
    }

    @GetMapping("/books/popular/more")
    @ResponseBody
    public CountedBooksDto getNextPopularPage(@RequestParam(value = "offset", required = false) Integer offset,
                                             @RequestParam(value = "limit", required = false) Integer limit) {
        UserDto userDto = getUserRegistrationService().getCurrentUserDto();
        return new CountedBooksDto(getBookService().getPopularBooks(offset, limit, userDto));
    }
}
