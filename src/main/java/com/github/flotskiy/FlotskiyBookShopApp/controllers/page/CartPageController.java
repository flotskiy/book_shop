package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.UserBookService;
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
    public String handleRemoveBookFromCartRequest(
            @PathVariable("slug") String slug,
            @CookieValue(name = "cartContents", required = false) String cartContents,
            HttpServletResponse response,
            Model model
    ) {
        if (cartContents != null && !cartContents.equals("")) {
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cartContents.split("/")));
            cookieBooks.remove(slug);
            Cookie cookie = new Cookie("cartContents", String.join("/", cookieBooks));
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);
        } else {
            model.addAttribute("isCartEmpty", true);
        }
        return "redirect:/books/cart";
    }
}
