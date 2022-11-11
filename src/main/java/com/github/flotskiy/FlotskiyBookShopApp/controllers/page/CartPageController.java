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
public class CartPageController extends HeaderController {

    private final UserBookService userBookService;

    @Autowired
    public CartPageController(
            UserRegistrationService userRegistrationService,
            BookService bookService,
            UserBookService userBookService
    ) {
        super(userRegistrationService, bookService);
        this.userBookService = userBookService;
    }

    @ModelAttribute(name = "bookCart")
    public List<BookDto> bookCart() {
        return new ArrayList<>();
    }

    @GetMapping("/books/cart")
    public String handleCartRequest(
            @CookieValue(value = "cartContents", required = false) String cartContents, Model model
            ) {
        if (!userBookService.isUserAuthenticated()) {
            userBookService.guestHandleCartRequest(cartContents, model, getUserRegistrationService().getCurrentUserId());
        } else {
            userBookService.registeredUserHandleCartRequest(model);
        }
        return "/cart";
    }

    @PostMapping("/books/changeBookStatus/cart/remove/{slug}")
    @ResponseBody
    @Transactional
    public Map<String, Object> handleRemoveBookFromCartRequest(
            @PathVariable("slug") String slug,
            @CookieValue(name = "cartContents", required = false) String cartContents,
            HttpServletResponse response,
            Model model
    ) {
        Map<String, Object> result = new HashMap<>();
        Integer userId = getUserRegistrationService().getCurrentUserId();
        try {
            userBookService.removeBookFromCartRequest(slug, cartContents, response, model, userId);
            result.put("result", true);
        } catch (Exception exception) {
            result.put("result", false);
            result.put("error", "Failed to remove book");
        }
        return result;
    }
}
