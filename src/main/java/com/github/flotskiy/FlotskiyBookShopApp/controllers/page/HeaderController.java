package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.HeaderInfoDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;

@Controller
public class HeaderController {

    private final UserRegistrationService userRegistrationService;
    private final BookService bookService;

    @Autowired
    public HeaderController(UserRegistrationService userRegistrationService, BookService bookService) {
        this.userRegistrationService = userRegistrationService;
        this.bookService = bookService;
    }

    @ModelAttribute("isAuthenticated")
    public Boolean isAuthenticated() {
        return bookService.isAuthenticated();
    }

    @ModelAttribute("headerInfoDto")
    public HeaderInfoDto headerInfoDto(HttpServletRequest request) {
        HeaderInfoDto headerInfoDto = new HeaderInfoDto();

        Cookie cartContents = WebUtils.getCookie(request, "cartContents");
        if (cartContents != null && !cartContents.getValue().equals("")) {
            String cartCookieString = cartContents.getValue();
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(cartCookieString.split("/")));
            headerInfoDto.setCartBooksCount(cookieBooks.size());
        }

        Cookie keptContents = WebUtils.getCookie(request, "keptContents");
        if (keptContents != null && !keptContents.getValue().equals("")) {
            String keptCookieString = keptContents.getValue();
            ArrayList<String> cookieBooks = new ArrayList<>(Arrays.asList(keptCookieString.split("/")));
            headerInfoDto.setKeptBooksCount(cookieBooks.size());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            UserDto userDto = userRegistrationService.gerCurrentUser();
            headerInfoDto.setMyBooksCount(5); // TODO: remove hardcode later
            headerInfoDto.setUserName(userDto.getName());
            headerInfoDto.setUserBalance(userDto.getBalance());
        }
        return headerInfoDto;
    }

    protected UserRegistrationService getUserRegistrationService() {
        return userRegistrationService;
    }

    protected BookService getBookService() {
        return bookService;
    }
}
