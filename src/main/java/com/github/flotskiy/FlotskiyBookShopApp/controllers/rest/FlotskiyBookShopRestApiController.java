package com.github.flotskiy.FlotskiyBookShopApp.controllers.rest;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.ApiResponse;
import com.github.flotskiy.FlotskiyBookShopApp.exceptions.BookstoreApiWrongParameterException;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.CountedBooksDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.GenreDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.page.TagDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.payments.PaymentDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.CountedBalanceTransactionsDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.*;
import com.github.flotskiy.FlotskiyBookShopApp.util.CustomStringHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Api(tags = "Book Data API")
@Tag(name = "Book Data API", description = "Controller performs API operations with Books")
public class FlotskiyBookShopRestApiController {

    private final BookService bookService;
    private final UserBookService userBookService;
    private final TagService tagService;
    private final GenreService genreService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final ReviewAndLikeService reviewAndLikeService;
    private final UserRegistrationService userRegistrationService;
    private final PaymentService paymentService;
    private final CustomStringHandler customStringHandler;

    @Autowired
    public FlotskiyBookShopRestApiController(
            BookService bookService,
            UserBookService userBookService,
            TagService tagService,
            GenreService genreService,
            BooksRatingAndPopularityService booksRatingAndPopularityService,
            ReviewAndLikeService reviewAndLikeService,
            UserRegistrationService userRegistrationService,
            PaymentService paymentService,
            CustomStringHandler customStringHandler
    ) {
        this.bookService = bookService;
        this.userBookService = userBookService;
        this.tagService = tagService;
        this.genreService = genreService;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.reviewAndLikeService = reviewAndLikeService;
        this.userRegistrationService = userRegistrationService;
        this.paymentService = paymentService;
        this.customStringHandler = customStringHandler;
    }

    @GetMapping("/books/by-author")
    @ApiOperation("Receiving List of Books with Specified Author's Name")
    public ResponseEntity<List<BookDto>> booksByAuthor(@RequestParam("author") String authorName) {
        int currentUserId = userRegistrationService.getCurrentUserId();
        return ResponseEntity.ok(bookService.getBooksByAuthor(authorName, currentUserId));
    }

    @GetMapping("/books/by-title")
    @ApiOperation("Receiving List of Books with Specified Book's Title")
    public ResponseEntity<ApiResponse<BookDto>> booksByTitle(@RequestParam("title") String title) {
        ApiResponse<BookDto> response = new ApiResponse<>();
        int currentUserId = userRegistrationService.getCurrentUserId();
        try {
            List<BookDto> data = bookService.getBooksByTitle(title, currentUserId);
            response.setDebugMessage("Successful request: /api/books/by-title");
            response.setMessage("Data size: " + data.size() + " books");
            response.setStatus(HttpStatus.OK);
            response.setData(data);
        } catch (Throwable throwable) {
            response.setDebugMessage("Request failed: /api/books/by-title");
            response.setMessage(throwable.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setData(null);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/books/by-price-range")
    @ApiOperation("Receiving List of Books with Specified Range of Prices")
    public ResponseEntity<List<BookDto>> priceRangeBooks(@RequestParam("min") int min, @RequestParam("max") int max) {
        int currentUserId = userRegistrationService.getCurrentUserId();
        return ResponseEntity.ok(bookService.getBooksWithPriceBetween(min, max, currentUserId));
    }

    @GetMapping("/books/with-max-discount")
    @ApiOperation("Receiving List of Books with Maximum Discount Value")
    public ResponseEntity<List<BookDto>> maxDiscountBooks() {
        int currentUserId = userRegistrationService.getCurrentUserId();
        return ResponseEntity.ok(bookService.getBooksWithMaxDiscount(currentUserId));
    }

    @GetMapping("/books/bestsellers")
    @ApiOperation("Receiving List of Books which are Bestsellers (if field 'is_bestseller' has value = 1)")
    public ResponseEntity<List<BookDto>> bestsellerBooks() {
        int currentUserId = userRegistrationService.getCurrentUserId();
        return ResponseEntity.ok(bookService.getBestsellers(currentUserId));
    }

    @GetMapping("/books/recommended")
    @ApiOperation("Receiving List of Books Recommended by special algorithm. " +
            "'offset' parameter is designed to set the first book of the list " +
            "and 'limit' parameter is helps to specify the number of books to show")
    public ResponseEntity<?> recommendedBooks(
            @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit
    ) {
        try {
            UserDto userDto = userRegistrationService.getCurrentUserDto();
            CountedBooksDto countedBooksDto =
                    new CountedBooksDto(bookService.getListOfRecommendedBooks(offset, limit, userDto));
            return ResponseEntity.ok().body(countedBooksDto);
        } catch (Throwable throwable) {
            HashMap<String, Object> result = new HashMap<>();
            result.put("result", true);
            result.put("error", throwable.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/books/recent")
    @ApiOperation("Receiving List of Books which were Published within the Last month " +
            "and Sorted by Date of publication descending. " +
            "Shows all books for guest user and not handled before books for registered user. " +
            "'offset' parameter is designed to set the first book of the list " +
            "and 'limit' parameter is helps to specify the number of books to show")
    public ResponseEntity<?> recentBooks(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam(value = "offset") Integer offset,
            @RequestParam(value = "limit") Integer limit
    ) {
        try {
            from = bookService.checkFrom(from);
            to = bookService.checkTo(to);
            DateTimeFormatter formatter = customStringHandler.getFormatter();
            UserDto userDto = userRegistrationService.getCurrentUserDto();
            CountedBooksDto countedBooksDto =
                    new CountedBooksDto(bookService.getPageOfRecentBooks(
                            LocalDate.parse(from, formatter), LocalDate.parse(to, formatter), offset, limit, userDto
                                        ).getContent()
                    );
            return ResponseEntity.ok().body(countedBooksDto);
        } catch (Throwable throwable) {
            HashMap<String, Object> result = new HashMap<>();
            result.put("result", true);
            result.put("error", throwable.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/books/popular")
    @ApiOperation("Receiving List of Popular Books Sorted by popularity descending. " +
            "'offset' parameter is designed to set the first book of the list " +
            "and 'limit' parameter is helps to specify the number of books to show")
    public ResponseEntity<?> popularBooks(
            @RequestParam(value = "offset") Integer offset, @RequestParam(value = "limit") Integer limit
    ) {
        try {
            UserDto userDto = userRegistrationService.getCurrentUserDto();
            CountedBooksDto countedBooksDto = new CountedBooksDto(bookService.getPopularBooks(offset, limit, userDto));
            return ResponseEntity.ok().body(countedBooksDto);
        } catch (Throwable throwable) {
            HashMap<String, Object> result = new HashMap<>();
            result.put("result", true);
            result.put("error", throwable.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/books/genres")
    @ApiOperation("Receiving List of GenresDto including all root and nested elements")
    public ResponseEntity<?> genres() {
        try {
            List<GenreDto> genreDtoList = genreService.getAllGenresTree();
            return ResponseEntity.ok().body(genreDtoList);
        } catch (Throwable throwable) {
            HashMap<String, Object> result = new HashMap<>();
            result.put("result", true);
            result.put("error", throwable.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/books/genre/{genreId}")
    @ApiOperation("Receiving List of Books matching a specific Genre. " +
            "'genreId' parameter represents the ID number of specific genre. " +
            "'offset' parameter is designed to set the first book of the list " +
            "and 'limit' parameter is helps to specify the number of books to show. " +
            "Books sorted by Date of publication descending.")
    public ResponseEntity<?> booksByGenre(
            @PathVariable("genreId") Integer genreId,
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit
    ) {
        try {
            int currentUserId = userRegistrationService.getCurrentUserId();
            CountedBooksDto countedBooksDto = new CountedBooksDto(
                    bookService.getPageOfBooksByGenreId(genreId, offset, limit, currentUserId)
                            .getContent()
            );
            return ResponseEntity.ok().body(countedBooksDto);
        } catch (Throwable throwable) {
            HashMap<String, Object> result = new HashMap<>();
            result.put("result", true);
            result.put("error", throwable.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/books/author/{authorId}")
    @ApiOperation("Receiving List of Books of definite author. " +
            "'authorId' parameter represents the ID number of definite author. " +
            "'offset' parameter is designed to set the first book of the list " +
            "and 'limit' parameter is helps to specify the number of books to show. " +
            "Books sorted by Date of publication descending.")
    public ResponseEntity<?> booksByAuthor(
            @PathVariable("authorId") Integer authorId,
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit
    ) {
        try {
            int currentUserId = userRegistrationService.getCurrentUserId();
            CountedBooksDto countedBooksDto = new CountedBooksDto(
                    bookService.getPageOfBooksByAuthorId(authorId, offset, limit, currentUserId).getContent()
            );
            return ResponseEntity.ok().body(countedBooksDto);
        } catch (Throwable throwable) {
            HashMap<String, Object> result = new HashMap<>();
            result.put("result", true);
            result.put("error", throwable.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/books/tags")
    @ApiOperation("Receiving List of All Tags with Title, Slug and Class. " +
            "Class depends from number of books marked by that tag and used to set font size for current tag")
    public ResponseEntity<?> tags() {
        try {
            List<TagDto> tagDtoList = tagService.getTagsList();
            return ResponseEntity.ok().body(tagDtoList);
        } catch (Throwable throwable) {
            HashMap<String, Object> result = new HashMap<>();
            result.put("result", true);
            result.put("error", throwable.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/books/tag/{tagId}")
    @ApiOperation("Receiving List of Books marked with a specific tag. " +
            "'tagId' parameter represents the ID number of specific tag. " +
            "'offset' parameter is designed to set the first book of the list " +
            "and 'limit' parameter is helps to specify the number of books to show. " +
            "Books sorted by Date of publication descending.")
    public ResponseEntity<?> taggedBooks(
            @PathVariable("tagId") Integer tagId,
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit
    ) {
        try {
            int currentUserId = userRegistrationService.getCurrentUserId();
            CountedBooksDto countedBooksDto = new CountedBooksDto(
                    bookService.getPageOfBooksByTag(tagId, offset, limit, currentUserId).getContent()
            );
            return ResponseEntity.ok().body(countedBooksDto);
        } catch (Throwable throwable) {
            HashMap<String, Object> result = new HashMap<>();
            result.put("result", true);
            result.put("error", throwable.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping(value = {"/books/search", "/books/search/{searchWord}"})
    @ApiOperation("Receiving List of Books matching a search query. " +
            "'offset' parameter is designed to set the first element of the list " +
            "and 'limit' parameter is helps to specify the number of books to show.")
    public ResponseEntity<?> search(
            @PathVariable(value = "searchWord", required = false) String searchWord,
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit
    ) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            int currentUserId = userRegistrationService.getCurrentUserId();
            CountedBooksDto countedBooksDto =
                    new CountedBooksDto(bookService.getPageOfSearchResultBooks(searchWord, offset, limit, currentUserId)
                            .getContent());
            return ResponseEntity.ok().body(countedBooksDto);
        } catch (Throwable throwable) {
            result.put("result", false);
            result.put("error", throwable.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @RequestMapping("/rateBook")
    @ApiOperation("Adds a Book's rate to Database from current User. " +
            "'bookId' parameter represents the ID number of definite book to rate. " +
            "'value' parameter is designed to set the level of rating from user. " +
            "Returns 'true' in case of success and 'false' in case of failure.")
    public ResponseEntity<Map<String, Object>> rateBook(
            @RequestParam("bookId") Integer bookId, @RequestParam("value") Integer value
    ) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            int currentUserId = userRegistrationService.getCurrentUserId();
            if (currentUserId == -1) {
                throw new UsernameNotFoundException("User is not authorized");
            }
            booksRatingAndPopularityService.setRatingToBookByUser(bookId, currentUserId, value);
            result.put("result", true);
            return ResponseEntity.ok().body(result);
        } catch (Throwable throwable) {
            result.put("result", false);
            result.put("error", throwable.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @RequestMapping("/bookReview")
    @ApiOperation("Adds review to a Book to Database from current authorized user. " +
            "'bookId' parameter represents the ID number of definite book to review. " +
            "'text' parameter is designed to receive review contents from user. " +
            "Returns 'true' in case of success and 'false' in case of failure.")
    public ResponseEntity<Map<String, Object>> bookReview(
            @RequestParam("bookId") Integer bookId, @RequestParam("text") String text
    ) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            int currentUserId = userRegistrationService.getCurrentUserId();
            if (currentUserId == -1) {
                throw new UsernameNotFoundException("User is not authorized");
            }
            reviewAndLikeService.bookReview(bookId, currentUserId, text);
            result.put("result", true);
            return ResponseEntity.ok().body(result);
        }  catch (Throwable throwable) {
            result.put("result", false);
            result.put("error", throwable.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @RequestMapping("/rateBookReview")
    @ApiOperation("Adds like or dislike to a Book review to Database from current User. " +
            "'reviewid' parameter represents the ID number of definite book review to like or dislike. " +
            "'value' parameter is designed to receive like (1) or dislike (-1) from user. " +
            "Returns 'true' in case of success and 'false' in case of failure.")
    public ResponseEntity<Map<String, Object>> rateBookReview(
            @RequestParam("reviewid") Integer reviewId, @RequestParam("value") Integer value
    ) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            int currentUserId = userRegistrationService.getCurrentUserId();
            if (currentUserId == -1) {
                throw new UsernameNotFoundException("User is not authorized");
            }
            booksRatingAndPopularityService.rateBookReview(reviewId, currentUserId, value);
            result.put("result", true);
            return ResponseEntity.ok().body(result);
        } catch (Throwable throwable) {
            result.put("result", false);
            result.put("error", throwable.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/transactions")
    @ApiOperation("Receiving List of transactions of current registered user. " +
            "'sort' parameter sets ordering the output in 'asc' or 'desc' order. " +
            "'offset' parameter is designed to set the first element of the list " +
            "and 'limit' parameter is helps to specify the number of transactions to show.")
    public ResponseEntity<?> transactions(
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit
    ) {
        try {
            Integer currentUserId = userRegistrationService.getCurrentUserId();
            if (currentUserId == -1) {
                throw new UsernameNotFoundException("User is not authorized");
            }
            CountedBalanceTransactionsDto countedBalanceTransactionsDto =
                    new CountedBalanceTransactionsDto(
                            paymentService.getBalanceTransactions(currentUserId, offset, limit, sort).getContent()
                    );
            return ResponseEntity.ok(countedBalanceTransactionsDto);
        } catch (Throwable throwable) {
            Map<String, Object> result = new HashMap<>();
            result.put("result", false);
            result.put("error", throwable.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @RequestMapping("/payment")
    @ApiOperation("Stores info about the transfer made through the payment system in a database. " +
            "'id' parameter represents the authorized user who makes the payment. " +
            "'sum' parameter indicates the amount of money to transfer. " +
            "'time' parameter fixes date and time of replenish operation.")
    public ResponseEntity<?> handleReplenish(
            @RequestParam("id") String id, @RequestParam("sum") String sum, @RequestParam("time") Long time
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (!userRegistrationService.isAuthenticated()) {
                throw new AuthenticationException("Not allowed to replenish for not authenticated user");
            } else if (userRegistrationService.getCurrentUserId() != Integer.parseInt(id)) {
                throw new AuthenticationException("Authenticated user id and id of user to replenish are different");
            }
            PaymentDto payload = new PaymentDto();
            payload.setId(id);
            payload.setSum(sum);
            payload.setTime(time);
            paymentService.makeReplenish(payload);
            String replenishUrl = paymentService.getReplenishUrl(payload.getSum());
            result.put("result", true);
            result.put("data", replenishUrl);
            return ResponseEntity.ok().body(result);
        } catch (Throwable throwable) {
            result.put("result", false);
            result.put("error", throwable.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }




    @RequestMapping("/changeBookStatus/{slug}")
    @ApiOperation("Changes status for one or several books in Database for current " +
            "'slug' parameter represents the definite book/books to change status. " +
            "'status' parameter is designed to receive from user the new value of book status to set." +
            "Returns 'true' in case of success and 'false' in case of failure.")
    public ResponseEntity<Map<String, Object>> changeBookStatus(
            @PathVariable(value = "slug") String slug,
            @RequestParam("status") String status,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        userBookService.guestChangeBookStatus(slug, status, request, response, model);
        HashMap<String, Object> result = new HashMap<>();
        result.put("result", true); // TODO: 'false' result in case of some problems
        return ResponseEntity.ok().body(result);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<BookDto>> handleMissingServletRequestParameterException(Exception exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(HttpStatus.BAD_REQUEST, "Missing required parameter", exception),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(BookstoreApiWrongParameterException.class)
    public ResponseEntity<ApiResponse<BookDto>> handleBookstoreApiWrongParameterException(Exception exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(
                        HttpStatus.BAD_REQUEST,
                        "Wrong value passed to parameter OR No data found with specified parameter",
                        exception
                ),
                HttpStatus.BAD_REQUEST
        );
    }
}
