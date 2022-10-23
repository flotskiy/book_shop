package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.util.CustomStringHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class PostponedPageController extends HeaderController {

    private final CustomStringHandler customStringHandler;

    @Autowired
    public PostponedPageController(
            UserRegistrationService userRegistrationService,
            BookService bookService,
            CustomStringHandler customStringHandler
    ) {
        super(userRegistrationService, bookService);
        this.customStringHandler = customStringHandler;
    }

    @ModelAttribute(name = "booksKept")
    public List<BookDto> booksKept() {
        return new ArrayList<>();
    }

    @ModelAttribute(name = "booksKeptSlugs")
    public List<String> booksKeptSlugs() {
        return new ArrayList<>();
    }

    @GetMapping("/books/postponed")
    public String handlePostponedRequest(
            @CookieValue(value = "keptContents", required = false) String keptContents,
            Model model
    ) {
        if (keptContents == null || keptContents.equals("")) {
            model.addAttribute("isKeptEmpty", true);
        } else {
            model.addAttribute("isKeptEmpty", false);
            String[] cookiesSlugs = customStringHandler.getCookieSlugs(keptContents);
            List<String> slugsList = List.of(cookiesSlugs);
            String slugsString = String.join(",", slugsList);
            List<BookDto> booksFromCookiesSlugs = getBookService().getBooksBySlugIn(slugsList);
            model.addAttribute("booksKept", booksFromCookiesSlugs);
            model.addAttribute("booksKeptSlugs", slugsString);
        }
        return "/postponed";
    }

    @PostMapping("/books/changeBookStatus/kept/remove/{slug}")
    public String handleRemoveBookFromKeptRequest(
            @PathVariable("slug") String slug,
            @CookieValue(name = "keptContents", required = false) String keptContents,
            HttpServletResponse response,
            Model model
    ) {
        if (keptContents != null && !keptContents.equals("")) {
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(keptContents.split("/")));
            cookieBooks.remove(slug);
            Cookie cookie = new Cookie("keptContents", String.join("/", cookieBooks));
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isKeptEmpty", false);
        } else {
            model.addAttribute("isKeptEmpty", true);
        }
        return "redirect:/books/postponed";
    }
}
