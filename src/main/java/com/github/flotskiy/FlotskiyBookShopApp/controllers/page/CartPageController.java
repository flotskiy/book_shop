package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.PaymentService;
import com.github.flotskiy.FlotskiyBookShopApp.service.UserBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
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

    @GetMapping("/books/pay")
    public RedirectView handlePay() throws NoSuchAlgorithmException {
        int userDtoId = getUserRegistrationService().getCurrentUserId();
        UserDto currentUser = getUserRegistrationService().getCurrentUserDtoById(userDtoId);
        List<BookDto> cartBooks = currentUser.getUserBooksData().getCart();
        String paymentUrl = paymentService.getPayment(cartBooks);
        return new RedirectView(paymentUrl);
    }
}
