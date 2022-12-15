package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.UserBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class PostponedPageController extends HeaderController {

    private final UserBookService userBookService;

    @Autowired
    public PostponedPageController(
            UserRegistrationService userRegistrationService, BookService bookService, UserBookService userBookService
    ) {
        super(userRegistrationService, bookService);
        this.userBookService = userBookService;
    }

    @ModelAttribute(name = "booksKept")
    public List<BookDto> booksKept() {
        return new ArrayList<>();
    }

    @GetMapping("/postponed")
    public String handlePostponedRequest(Model model) {
        if (!userBookService.isUserAuthenticated()) {
            userBookService.guestHandlePostponedRequest(model);
        } else {
            userBookService.registeredUserHandlePostponedRequest(model);
        }
        return "/postponed";
    }
}
