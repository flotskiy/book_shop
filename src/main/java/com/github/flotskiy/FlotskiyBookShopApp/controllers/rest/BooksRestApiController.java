package com.github.flotskiy.FlotskiyBookShopApp.controllers.rest;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.ApiResponse;
import com.github.flotskiy.FlotskiyBookShopApp.exceptions.BookstoreApiWrongParameterException;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.CountedBooksDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.GenreDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.page.TagDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.user.UserDto;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/books")
@Api(tags = "Book Data API")
@Tag(name = "Book Data API", description = "Controller performs API operations with Books")
public class BooksRestApiController {

    private final BookService bookService;
    private final UserBookService userBookService;
    private final TagService tagService;
    private final GenreService genreService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final ReviewAndLikeService reviewAndLikeService;
    private final UserRegistrationService userRegistrationService;

    @Autowired
    public BooksRestApiController(
            BookService bookService,
            UserBookService userBookService,
            TagService tagService,
            GenreService genreService,
            BooksRatingAndPopularityService booksRatingAndPopularityService,
            ReviewAndLikeService reviewAndLikeService,
            UserRegistrationService userRegistrationService
            ) {
        this.bookService = bookService;
        this.userBookService = userBookService;
        this.tagService = tagService;
        this.genreService = genreService;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.reviewAndLikeService = reviewAndLikeService;
        this.userRegistrationService = userRegistrationService;
    }

    @GetMapping("/by-author")
    @ApiOperation("Receiving List of Books with Specified Author's Name")
    public ResponseEntity<List<BookDto>> booksByAuthor(@RequestParam("author") String authorName) {
        int currentUserId = userRegistrationService.getCurrentUserId();
        return ResponseEntity.ok(bookService.getBooksByAuthor(authorName, currentUserId));
    }

    @GetMapping("/by-title")
    @ApiOperation("Receiving List of Books with Specified Book's Title")
    public ResponseEntity<ApiResponse<BookDto>> booksByTitle(@RequestParam("title") String title)
            throws BookstoreApiWrongParameterException {
        ApiResponse<BookDto> response = new ApiResponse<>();
        int currentUserId = userRegistrationService.getCurrentUserId();
        List<BookDto> data = bookService.getBooksByTitle(title, currentUserId);
        response.setDebugMessage("Successful request: /api/books/by-title");
        response.setMessage("Data size: " + data.size() + " books");
        response.setStatus(HttpStatus.OK);
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-price-range")
    @ApiOperation("Receiving List of Books with Specified Range of Prices")
    public ResponseEntity<List<BookDto>> priceRangeBooks(@RequestParam("min") int min, @RequestParam("max") int max) {
        int currentUserId = userRegistrationService.getCurrentUserId();
        return ResponseEntity.ok(bookService.getBooksWithPriceBetween(min, max, currentUserId));
    }

    @GetMapping("/with-max-discount")
    @ApiOperation("Receiving List of Books with Maximum Discount Value")
    public ResponseEntity<List<BookDto>> maxDiscountBooks() {
        int currentUserId = userRegistrationService.getCurrentUserId();
        return ResponseEntity.ok(bookService.getBooksWithMaxDiscount(currentUserId));
    }

    @GetMapping("/bestsellers")
    @ApiOperation("Receiving List of Books which are Bestsellers (if field 'is_bestseller' has value = 1)")
    public ResponseEntity<List<BookDto>> bestsellerBooks() {
        int currentUserId = userRegistrationService.getCurrentUserId();
        return ResponseEntity.ok(bookService.getBestsellers(currentUserId));
    }

    @GetMapping("/recommended")
    @ApiOperation("Receiving List of Books Recommended by special algorithm. " +
            "'offset' parameter is designed to set the first book of the list " +
            "and 'limit' parameter is helps to specify the number of books to show")
    public ResponseEntity<CountedBooksDto> recommendedBooks(
            @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit
    ) {
        int currentUserId = userRegistrationService.getCurrentUserId();
        UserDto currentUserDto = userRegistrationService.getCurrentUserDtoById(currentUserId);
        return ResponseEntity
                .ok(new CountedBooksDto(bookService.getListOfRecommendedBooks(offset, limit, currentUserId, currentUserDto)));
    }

    @GetMapping("/recent")
    @ApiOperation("Receiving List of Books which were Published within the Last month " +
            "and Sorted by Date of publication descending." +
            "'offset' parameter is designed to set the first book of the list " +
            "and 'limit' parameter is helps to specify the number of books to show")
    public ResponseEntity<CountedBooksDto> recentBooks(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam(value = "offset") Integer offset,
            @RequestParam(value = "limit") Integer limit
    ) {
        from = bookService.checkFrom(from);
        to = bookService.checkTo(to);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        int currentUserId = userRegistrationService.getCurrentUserId();
        return ResponseEntity.ok(
                new CountedBooksDto(bookService.getPageOfRecentBooks(
                        LocalDate.parse(from, formatter), LocalDate.parse(to, formatter), offset, limit, currentUserId
                ).getContent()));
    }

    @GetMapping("/popular")
    @ApiOperation("Receiving List of Popular Books Sorted by Rating descending. " +
            "'offset' parameter is designed to set the first book of the list " +
            "and 'limit' parameter is helps to specify the number of books to show")
    public ResponseEntity<CountedBooksDto> popularBooks(@RequestParam(value = "offset") Integer offset,
                                                      @RequestParam(value = "limit") Integer limit) {
        int currentUserId = userRegistrationService.getCurrentUserId();
        return ResponseEntity.ok(new CountedBooksDto(bookService.getPopularBooks(offset, limit, currentUserId)));
    }

    @GetMapping("/tags")
    @ApiOperation("Receiving List of All Tags with Title, Slug and Class. " +
            "Class depends from number of books marked by that tag and used to set font size for current tag")
    public ResponseEntity<List<TagDto>> tags() {
        return ResponseEntity.ok(tagService.getTagsList());
    }

    @GetMapping("/tag/{tagId}")
    @ApiOperation("Receiving List of Books marked with a specific tag. " +
            "'tagId' parameter represents the ID number of specific tag. " +
            "'offset' parameter is designed to set the first book of the list " +
            "and 'limit' parameter is helps to specify the number of books to show. " +
            "Books sorted by Date of publication descending.")
    public ResponseEntity<CountedBooksDto> taggedBooks(
            @PathVariable("tagId") Integer tagId,
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit
    ) {
        int currentUserId = userRegistrationService.getCurrentUserId();
        return ResponseEntity
                .ok(new CountedBooksDto(bookService.getPageOfBooksByTag(tagId, offset, limit, currentUserId).getContent()));
    }

    @GetMapping("/genres")
    @ApiOperation("Receiving List of GenresDto including all root and nested elements")
    public ResponseEntity<List<GenreDto>> genres() {
        return ResponseEntity.ok(genreService.getAllGenresTree());
    }

    @GetMapping("/genre/{genreId}")
    @ApiOperation("Receiving List of Books matching a specific Genre. " +
            "'genreId' parameter represents the ID number of specific genre. " +
            "'offset' parameter is designed to set the first book of the list " +
            "and 'limit' parameter is helps to specify the number of books to show. " +
            "Books sorted by Date of publication descending.")
    public ResponseEntity<CountedBooksDto> booksByGenre(
            @PathVariable("genreId") Integer genreId,
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit
    ) {
        int currentUserId = userRegistrationService.getCurrentUserId();
        return ResponseEntity
                .ok(new CountedBooksDto(bookService.getPageOfBooksByGenreId(genreId, offset, limit, currentUserId)
                        .getContent()));
    }

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    @ApiOperation("Receiving List of Books matching a search query. " +
            "'offset' parameter is designed to set the first element of the list " +
            "and 'limit' parameter is helps to specify the number of books to show.")
    public ResponseEntity<CountedBooksDto> getNextSearchPage(
            @PathVariable(value = "searchWord", required = false) String searchWord,
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit
    ) {
        int currentUserId = userRegistrationService.getCurrentUserId();
        return ResponseEntity
                .ok(new CountedBooksDto(bookService.getPageOfSearchResultBooks(searchWord, offset, limit, currentUserId)
                        .getContent()));
    }

    @GetMapping("/author/{authorId}")
    @ApiOperation("Receiving List of Books of definite author. " +
            "'authorId' parameter represents the ID number of definite author. " +
            "'offset' parameter is designed to set the first book of the list " +
            "and 'limit' parameter is helps to specify the number of books to show. " +
            "Books sorted by Date of publication descending.")
    public ResponseEntity<CountedBooksDto> booksByAuthor(
            @PathVariable("authorId") Integer authorId,
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit
    ) {
        int currentUserId = userRegistrationService.getCurrentUserId();
        return ResponseEntity
                .ok(new CountedBooksDto(bookService.getPageOfBooksByAuthorId(authorId, offset, limit, currentUserId)
                        .getContent()));
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

    @RequestMapping("/rateBook")
    @ApiOperation("Adds a Book's rate to Database from current User. " +
            "'bookId' parameter represents the ID number of definite book to rate. " +
            "'value' parameter is designed to set the level of rating from user. " +
            "Returns 'true' in case of success and 'false' in case of failure.")
    public ResponseEntity<Map<String, Object>> rateCurrentBook(
            @RequestParam("bookId") Integer bookId, @RequestParam("value") Integer value
    ) {
        int currentUserId = userRegistrationService.getCurrentUserId();
        HashMap<String, Object> result = new HashMap<>();
        try {
            booksRatingAndPopularityService.setRatingToBookByUser(bookId, currentUserId, value);
            result.put("result", true);
            return ResponseEntity.ok().body(result);
        } catch (Exception ex) {
            result.put("result", false);
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
        int currentUserId = userRegistrationService.getCurrentUserId();
        HashMap<String, Object> result = new HashMap<>();
        try {
            booksRatingAndPopularityService.rateBookReview(reviewId, currentUserId, value);
            result.put("result", true);
            return ResponseEntity.ok().body(result);
        } catch (Exception ex) {
            result.put("result", false);
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
        int currentUserId = userRegistrationService.getCurrentUserId();
        HashMap<String, Object> result = new HashMap<>();
        try {
            reviewAndLikeService.bookReview(bookId, currentUserId, text);
            result.put("result", true);
            return ResponseEntity.ok().body(result);
        }  catch (Exception ex) {
            result.put("result", false);
            result.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<BookDto>> handleMissingServletRequestParameterException(Exception exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(HttpStatus.BAD_REQUEST, "Missing required parameter 'title'", exception),
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
