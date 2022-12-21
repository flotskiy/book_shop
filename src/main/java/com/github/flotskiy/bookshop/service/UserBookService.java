package com.github.flotskiy.bookshop.service;

import com.github.flotskiy.bookshop.exceptions.ChangeBookStatusException;
import com.github.flotskiy.bookshop.exceptions.ChangeBookStatusRedirectionException;
import com.github.flotskiy.bookshop.exceptions.RegisteredUserChangeBookStatusException;
import com.github.flotskiy.bookshop.model.dto.book.AuthorDto;
import com.github.flotskiy.bookshop.model.dto.book.BookDto;
import com.github.flotskiy.bookshop.model.dto.book.page.BookDtoExtension;
import com.github.flotskiy.bookshop.model.dto.user.UserDto;
import com.github.flotskiy.bookshop.model.entity.book.BookEntity;
import com.github.flotskiy.bookshop.model.entity.book.links.Book2UserEntity;
import com.github.flotskiy.bookshop.security.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@PropertySource("application-variables.properties")
public class UserBookService {

    private static final String IS_CART_EMPTY = "isCartEmpty";
    private static final String IS_KEPT_EMPTY = "isKeptEmpty";

    @Value("${book.status.delete}")
    private String unlink;

    @Value("${book.status.kept}")
    private String kept;

    @Value("${book.status.cart}")
    private String cart;

    @Value("${book.status.paid}")
    private String paid;

    @Value("${book.status.archived}")
    private String archived;

    private final Book2UserService book2UserService;
    private final UserRegistrationService userRegistrationService;
    private final BookService bookService;
    private final AuthorService authorService;

    @Autowired
    public UserBookService(
            Book2UserService book2UserService,
            UserRegistrationService userRegistrationService,
            BookService bookService,
            AuthorService authorService
    ) {
        this.book2UserService = book2UserService;
        this.userRegistrationService = userRegistrationService;
        this.bookService = bookService;
        this.authorService = authorService;
    }

    public boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken);
    }

    public void guestHandleCartRequest(Model model) {
        String guestSession = RequestContextHolder.currentRequestAttributes().getSessionId();
        if (userRegistrationService.isGuestKnown(guestSession)) {
            UserDto currentUser = userRegistrationService.getCurrentGuestUserDto(guestSession);
            handleUserDtoOnCartPage(currentUser, model);
        } else {
            model.addAttribute(IS_CART_EMPTY, true);
        }
    }

    public void registeredUserHandleCartRequest(Model model) {
        UserDto currentUser = userRegistrationService.getCurrentUserDto();
        handleUserDtoOnCartPage(currentUser, model);
    }

    private void handleUserDtoOnCartPage(UserDto userDto, Model model) {
        List<BookDto> cartBooks = userDto.getUserBooksData().getCart();
        if (cartBooks.isEmpty()) {
            model.addAttribute(IS_CART_EMPTY, true);
        } else {
            model.addAttribute(IS_CART_EMPTY, false);
            model.addAttribute("bookCart", convertListOfBooksToMap(cartBooks));
        }
    }

    public void guestHandlePostponedRequest(Model model) {
        String guestSession = RequestContextHolder.currentRequestAttributes().getSessionId();
        if (userRegistrationService.isGuestKnown(guestSession)) {
            UserDto currentUser = userRegistrationService.getCurrentGuestUserDto(guestSession);
            handleUserDtoOnPostponedPage(currentUser, model);
        } else {
            model.addAttribute(IS_KEPT_EMPTY, true);
        }
    }

    public void registeredUserHandlePostponedRequest(Model model) {
        UserDto currentUser = userRegistrationService.getCurrentUserDto();
        handleUserDtoOnPostponedPage(currentUser, model);
    }

    private void handleUserDtoOnPostponedPage(UserDto userDto, Model model) {
        List<BookDto> postponedBooks = userDto.getUserBooksData().getKept();
        if (postponedBooks.isEmpty()) {
            model.addAttribute(IS_KEPT_EMPTY, true);
        } else {
            List<Integer> postponedBooksIdList = postponedBooks.stream().map(BookDto::getId).collect(Collectors.toList());
            model.addAttribute("booksKeptIds", postponedBooksIdList);
            model.addAttribute(IS_KEPT_EMPTY, false);
            model.addAttribute("booksKept", convertListOfBooksToMap(postponedBooks));
        }
    }

    public void handleUnreadRequest(Model model) {
        UserDto currentUser = userRegistrationService.getCurrentUserDto();
        List<BookDto> unreadBooks = currentUser.getUserBooksData().getPaid();
        if (unreadBooks.isEmpty()) {
            model.addAttribute("isUnreadEmpty", true);
        } else {
            model.addAttribute("isUnreadEmpty", false);
            model.addAttribute("booksUnread", unreadBooks);
        }
    }

    public void handleArchivedRequest(Model model) {
        UserDto currentUser = userRegistrationService.getCurrentUserDto();
        List<BookDto> archivedBooks = currentUser.getUserBooksData().getArchived();
        if (archivedBooks.isEmpty()) {
            model.addAttribute("isArchivedEmpty", true);
        } else {
            model.addAttribute("isArchivedEmpty", false);
            model.addAttribute("booksArchived", archivedBooks);
        }
    }

    @Transactional
    public void changeBookStatus(List<Integer> bookIdList, String status)
            throws RegisteredUserChangeBookStatusException {
        int userId = userRegistrationService.getCurrentUserIdIncludingGuest();
        if (isUserAuthenticated()) {
            registeredUserChangeBookStatus(bookIdList, status, userId);
        } else {
            guestChangeBookStatus(bookIdList, status);
        }
    }

    public void registeredUserChangeBookStatus(List<Integer> bookIdList, String newStatus, Integer userId) {
        if (newStatus.equals("/cart")) {
            throw new ChangeBookStatusRedirectionException("Book status change cancelled, redirected to: " + newStatus);
        }
        for (Integer bookId : bookIdList) {
            handleBookId(bookId, userId, newStatus);
        }
    }

    public void guestChangeBookStatus(List<Integer> bookIdList, String status) {
        String guestSession = RequestContextHolder.currentRequestAttributes().getSessionId();
        int guestUserId = userRegistrationService.handleGuestSession(guestSession);
        registeredUserChangeBookStatus(bookIdList, status, guestUserId);
    }

    private Map<BookDto, BookDtoExtension> convertListOfBooksToMap(List<BookDto> bookDtoList) {
        Map<BookDto, BookDtoExtension> cartBooksMap = new HashMap<>();
        for (BookDto bookDto : bookDtoList) {
            BookEntity bookEntity = bookService.getBookEntityById(bookDto.getId());
            List<AuthorDto> authorDtoList = bookEntity.getAuthorEntities().stream()
                    .map(authorService::convertAuthorEntityToAuthorDtoShort).collect(Collectors.toList());
            BookDtoExtension bookDtoExtension = new BookDtoExtension();
            bookDtoExtension.setBookIdList(List.of(bookDto.getId()));
            bookDtoExtension.setAuthorList(authorDtoList);
            cartBooksMap.put(bookDto, bookDtoExtension);
        }
        return cartBooksMap;
    }

    private void handleBookId(Integer currentBookId, Integer currentUserId, String status) {
        Book2UserEntity book2UserEntity = book2UserService.findBook2UserEntityByBookIdAndUserId(currentBookId, currentUserId);
        if (book2UserEntity == null && status.equals(unlink)) {
            throw new ChangeBookStatusException("Nothing to unlink");
        }
        if (book2UserEntity != null) {
            Integer currentStatusId = book2UserEntity.getTypeId();
            String currentStatusString = book2UserService.findBookStatusStringById(currentStatusId);
            if (status.equals(unlink)) {
                if (currentStatusString.equals(paid) || currentStatusString.equals(archived)) {
                    throw new RegisteredUserChangeBookStatusException("Impossible to delete a purchased or archived book");
                }
                book2UserService.removeBook2UserEntry(currentBookId, currentUserId);
                return;
            }
            if (isOperationProhibitedForPurchasedBook(currentStatusString, status)) {
                throw new RegisteredUserChangeBookStatusException("Impossible to keep or cart book with paid or archived status");
            }
            Integer newStatusId = book2UserService.getBookToUserTypeMap().get(status);
            if (newStatusId.equals(book2UserEntity.getTypeId())) {
                throw new ChangeBookStatusException("Setting a similar status for a book");
            }
        }
        book2UserEntity = book2UserService.saveBook2UserEntry(status, currentBookId, currentUserId);
        if (book2UserEntity == null) {
            throw new RegisteredUserChangeBookStatusException("New book status set FAILED: book not linked to user");
        }
    }

    private boolean isOperationProhibitedForPurchasedBook(String currentStatus, String newStatus) {
        return (currentStatus.equals(paid) || currentStatus.equals(archived)) &&
                (newStatus.equals(kept) || newStatus.equals(cart));
    }
}
