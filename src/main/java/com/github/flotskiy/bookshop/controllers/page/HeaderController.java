package com.github.flotskiy.bookshop.controllers.page;

import com.github.flotskiy.bookshop.model.dto.HeaderInfoDto;
import com.github.flotskiy.bookshop.model.dto.user.UserDto;
import com.github.flotskiy.bookshop.security.UserRegistrationService;
import com.github.flotskiy.bookshop.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;

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
        return userRegistrationService.isAuthenticated();
    }

    @ModelAttribute("headerInfoDto")
    public HeaderInfoDto headerInfoDto() {
        HeaderInfoDto headerInfoDto = new HeaderInfoDto();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            String guestSession = RequestContextHolder.currentRequestAttributes().getSessionId();
            if (userRegistrationService.isGuestKnown(guestSession)) {
                UserDto userDto = userRegistrationService.getCurrentGuestUserDto(guestSession);
                headerInfoDto.setCartBooksCount(userDto.getUserBooksData().getCart().size());
                headerInfoDto.setKeptBooksCount(userDto.getUserBooksData().getKept().size());
            }
        } else {
            UserDto userDto = userRegistrationService.getCurrentUserDto();
            headerInfoDto.setMyBooksCount(userDto.getUserBooksData().getPaid().size());
            headerInfoDto.setCartBooksCount(userDto.getUserBooksData().getCart().size());
            headerInfoDto.setKeptBooksCount(userDto.getUserBooksData().getKept().size());
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
