package com.github.flotskiy.FlotskiyBookShopApp.service;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.links.Book2UserEntity;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.util.CustomStringHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class UserBookService {

    private final CustomStringHandler customStringHandler;
    private final BookService bookService;
    private final Book2UserService book2UserService;
    private final UserRegistrationService userRegistrationService;
    private final CookieService cookieService;
    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Autowired
    public UserBookService(
            CustomStringHandler customStringHandler,
            BookService bookService,
            Book2UserService book2UserService,
            UserRegistrationService userRegistrationService,
            CookieService cookieService
    ) {
        this.customStringHandler = customStringHandler;
        this.bookService = bookService;
        this.book2UserService = book2UserService;
        this.userRegistrationService = userRegistrationService;
        this.cookieService = cookieService;
    }

    public boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken);
    }

    public void guestHandleCartRequest(String cartContents, Model model, Integer userId) {
        if (cartContents == null || cartContents.equals("")) {
            model.addAttribute("isCartEmpty", true);
        } else {
            model.addAttribute("isCartEmpty", false);
            String[] cookiesSlugs = customStringHandler.getCookieSlugs(cartContents);
            List<BookDto> booksFromCookiesSlugs = bookService.getBooksBySlugIn(List.of(cookiesSlugs), userId);
            model.addAttribute("bookCart", booksFromCookiesSlugs);
        }
    }

    public void registeredUserHandleCartRequest(Model model) {
        int userDtoId = userRegistrationService.getCurrentUserId();
        UserDto currentUser = userRegistrationService.getCurrentUserDtoById(userDtoId);
        List<BookDto> cartBooks = currentUser.getUserBooksData().getCart();
        if (cartBooks.isEmpty()) {
            model.addAttribute("isCartEmpty", true);
        } else {
            model.addAttribute("isCartEmpty", false);
            model.addAttribute("bookCart", cartBooks);
        }
    }

    public void guestHandlePostponedRequest(String keptContents, Model model, Integer userId) {
        if (keptContents == null || keptContents.equals("")) {
            model.addAttribute("isKeptEmpty", true);
        } else {
            model.addAttribute("isKeptEmpty", false);
            String[] cookiesSlugs = customStringHandler.getCookieSlugs(keptContents);
            List<String> slugsList = List.of(cookiesSlugs);
            String slugsString = String.join(",", slugsList);
            List<BookDto> booksFromCookiesSlugs = bookService.getBooksBySlugIn(slugsList, userId);
            model.addAttribute("booksKept", booksFromCookiesSlugs);
            model.addAttribute("booksKeptSlugs", slugsString);
        }
    }

    public void registeredUserHandlePostponedRequest(Model model) {
        int userDtoId = userRegistrationService.getCurrentUserId();
        UserDto currentUser = userRegistrationService.getCurrentUserDtoById(userDtoId);
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

    public void changeBookStatus(
            String slug, String status, HttpServletRequest request, HttpServletResponse response, Model model, Integer userId
    ) {
        if (isUserAuthenticated()) {
            logger.info("change book status for authenticated user");
            registeredUserChangeBookStatus(slug, status, userId);
        } else {
            logger.info("change book status for guest");
            guestChangeBookStatus(slug, status, request, response, model);
        }
    }

    public void guestChangeBookStatus(
            String slug, String status, HttpServletRequest request, HttpServletResponse response, Model model
    ) {
        String newCookie = slug.replaceAll(",", "/");
        String cartContents = "";
        String keptContents = "";
        Cookie[] cookiesFromRequest = request.getCookies();

        if (cookiesFromRequest != null) {
            Map<String, String> cookiesMap = Arrays.stream(cookiesFromRequest)
                    .collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
            cartContents = cookiesMap.get("cartContents");
            keptContents = cookiesMap.get("keptContents");
        }
        if (status.equals("CART")) {
            cookieService.handleCartCookie(cartContents, newCookie, response, model);
        } else if (status.equals("KEPT")) {
            cookieService.handleKeptCookie(keptContents, newCookie, response, model);
        }
    }

    private Book2UserEntity registeredUserChangeBookStatus(String slug, String status, Integer userId) {
        Book2UserEntity lastBook2UserEntityWithChangedStatus = null;
        String[] singleBookSlugs = slug.split(",");
        for (String singleBookSlug : singleBookSlugs) {
            Integer bookId = bookService.getBookEntityBySlug(singleBookSlug).getId();
            lastBook2UserEntityWithChangedStatus = book2UserService.saveBook2UserEntry(status, bookId, userId);
        }
        return lastBook2UserEntityWithChangedStatus;
    }

    public void removeBookFromCartRequest(
            String slug, String cartContents, HttpServletResponse response, Model model, Integer userId
    ) {
        if (isUserAuthenticated()) {
            logger.info("remove book from CART for authenticated user");
            registeredUserRemoveBook(slug, userId);
        } else {
            logger.info("remove book from CART for guest");
            guestRemoveBookFromCartRequest(slug, cartContents, response, model);
        }
    }

    public void guestRemoveBookFromCartRequest(
            String slug, String cartContents, HttpServletResponse response, Model model
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
    }

    public void removeBookFromKeptRequest(
            String slug, String cartContents, HttpServletResponse response, Model model, Integer userId
    ) {
        if (isUserAuthenticated()) {
            logger.info("remove book from POSTPONED for authenticated user");
            registeredUserRemoveBook(slug, userId);
        } else {
            logger.info("remove book from POSTPONED for guest");
            guestRemoveBookFromKeptRequest(slug, cartContents, response, model);
        }
    }

    public void guestRemoveBookFromKeptRequest(
            String slug, String keptContents, HttpServletResponse response, Model model
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
    }

    public void registeredUserRemoveBook(String slug, Integer userId) {
        Integer bookId = bookService.getBookEntityBySlug(slug).getId();
        book2UserService.removeBook2UserEntry(bookId, userId);
    }
}
