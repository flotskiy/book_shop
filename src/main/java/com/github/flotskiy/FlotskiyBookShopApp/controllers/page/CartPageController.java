package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.PaymentService;
import com.github.flotskiy.FlotskiyBookShopApp.service.UserBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class CartPageController extends HeaderController {

    private final UserBookService userBookService;
    private final PaymentService paymentService;

    @Autowired
    public CartPageController(
            UserRegistrationService userRegistrationService,
            BookService bookService,
            UserBookService userBookService,
            PaymentService paymentService
    ) {
        super(userRegistrationService, bookService);
        this.userBookService = userBookService;
        this.paymentService = paymentService;
    }

    @ModelAttribute(name = "bookCart")
    public List<BookDto> bookCart() {
        return new ArrayList<>();
    }

    @GetMapping("/books/cart")
    public String handleCartRequest(Model model) {
        if (!userBookService.isUserAuthenticated()) {
            userBookService.guestHandleCartRequest(model);
        } else {
            userBookService.registeredUserHandleCartRequest(model);
        }
        return "/cart";
    }

    @GetMapping("/books/pay")
    public String handlePayAndRedirect(HttpServletRequest request) {
        UserDto currentUser = getUserRegistrationService().getCurrentUserDto();
        try {
            paymentService.pay(currentUser);
            return "redirect:/my";
        } catch (RuntimeException runtimeException) {
            request.setAttribute("pay", "fail");
            return "forward:/books/cart";
        }
    }
}
