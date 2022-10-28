package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.util.CustomStringHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserBookService {

    private final CustomStringHandler customStringHandler;
    private final BookService bookService;
    private final UserRegistrationService userRegistrationService;

    @Autowired
    public UserBookService(
            CustomStringHandler customStringHandler, BookService bookService, UserRegistrationService userRegistrationService) {
        this.customStringHandler = customStringHandler;
        this.bookService = bookService;
        this.userRegistrationService = userRegistrationService;
    }

    public boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken);
    }

    public void guestHandleCartRequest(String cartContents, Model model) {
        if (cartContents == null || cartContents.equals("")) {
            model.addAttribute("isCartEmpty", true);
        } else {
            model.addAttribute("isCartEmpty", false);
            String[] cookiesSlugs = customStringHandler.getCookieSlugs(cartContents);
            List<BookDto> booksFromCookiesSlugs = bookService.getBooksBySlugIn(List.of(cookiesSlugs));
            model.addAttribute("bookCart", booksFromCookiesSlugs);
        }
    }

    public void registeredUserHandleCartRequest(Model model) {
        UserDto currentUser = userRegistrationService.gerCurrentUser();
        List<BookDto> cartBooks = currentUser.getUserBooksData().getCart();
        if (cartBooks.isEmpty()) {
            model.addAttribute("isCartEmpty", true);
        } else {
            model.addAttribute("isCartEmpty", false);
            model.addAttribute("bookCart", cartBooks);
        }
    }

    public void guestHandlePostponedRequest(String keptContents, Model model) {
        if (keptContents == null || keptContents.equals("")) {
            model.addAttribute("isKeptEmpty", true);
        } else {
            model.addAttribute("isKeptEmpty", false);
            String[] cookiesSlugs = customStringHandler.getCookieSlugs(keptContents);
            List<String> slugsList = List.of(cookiesSlugs);
            String slugsString = String.join(",", slugsList);
            List<BookDto> booksFromCookiesSlugs = bookService.getBooksBySlugIn(slugsList);
            model.addAttribute("booksKept", booksFromCookiesSlugs);
            model.addAttribute("booksKeptSlugs", slugsString);
        }
    }

    public void registeredUserHandlePostponedRequest(Model model) {
        UserDto currentUser = userRegistrationService.gerCurrentUser();
        List<BookDto> postponedBooks = currentUser.getUserBooksData().getKept();
        if (postponedBooks.isEmpty()) {
            model.addAttribute("isKeptEmpty", true);
        } else {
            List<String> slugsList = postponedBooks.stream().map(BookDto::getSlug).collect(Collectors.toList());
            String slugsString = String.join(",", slugsList);
            model.addAttribute("booksKeptSlugs", slugsString);
            model.addAttribute("isKeptEmpty", false);
            model.addAttribute("booksKept", postponedBooks);
        }
    }

    public void handleUnreadRequest(Model model) {
        UserDto currentUser = userRegistrationService.gerCurrentUser();
        List<BookDto> unreadBooks = currentUser.getUserBooksData().getPaid();
        if (unreadBooks.isEmpty()) {
            model.addAttribute("isUnreadEmpty", true);
        } else {
            model.addAttribute("isUnreadEmpty", false);
            model.addAttribute("booksUnread", unreadBooks);
        }
    }

    public void handleArchivedRequest(Model model) {
        UserDto currentUser = userRegistrationService.gerCurrentUser();
        List<BookDto> archivedBooks = currentUser.getUserBooksData().getArchived();
        if (archivedBooks.isEmpty()) {
            model.addAttribute("isArchivedEmpty", true);
        } else {
            model.addAttribute("isArchivedEmpty", false);
            model.addAttribute("booksArchived", archivedBooks);
        }
    }
}
