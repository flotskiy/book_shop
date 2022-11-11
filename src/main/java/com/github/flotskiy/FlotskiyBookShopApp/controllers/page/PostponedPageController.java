package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.UserBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class PostponedPageController extends HeaderController {

    private final UserBookService userBookService;

    @Autowired
    public PostponedPageController(
            UserRegistrationService userRegistrationService,
            BookService bookService,
            UserBookService userBookService
    ) {
        super(userRegistrationService, bookService);
        this.userBookService = userBookService;
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
        if (!userBookService.isUserAuthenticated()) {
            userBookService.guestHandlePostponedRequest(keptContents, model, getUserRegistrationService().getCurrentUserId());
        } else {
            userBookService.registeredUserHandlePostponedRequest(model);
        }
        return "/postponed";
    }

    @PostMapping("/books/changeBookStatus/kept/remove/{slug}")
    @ResponseBody
    @Transactional
    public Map<String, Object> handleRemoveBookFromKeptRequest(
            @PathVariable("slug") String slug,
            @CookieValue(name = "keptContents", required = false) String keptContents,
            HttpServletResponse response,
            Model model
    ) {
        Map<String, Object> result = new HashMap<>();
        Integer userId = getUserRegistrationService().getCurrentUserId();
        try {
            userBookService.removeBookFromKeptRequest(slug, keptContents, response, model, userId);
            result.put("result", true);
        } catch (Exception exception) {
            result.put("result", false);
            result.put("error", "Failed to remove book");
        }
        return result;
    }
}
