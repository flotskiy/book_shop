package com.github.flotskiy.FlotskiyBookShopApp.controllers.rest;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.TagDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Api(tags = "Book Data API")
@Tag(name = "Book Data API", description = "Controller performs API operations with Books")
public class BooksRestApiController {

    private final BookService bookService;
    private final TagService tagService;

    @Autowired
    public BooksRestApiController(BookService bookService, TagService tagService) {
        this.bookService = bookService;
        this.tagService = tagService;
    }

    @GetMapping("/by-author")
    @ApiOperation("Receiving List of Books with Specified Author's Name")
    public ResponseEntity<List<BookDto>> booksByAuthor(@RequestParam("author") String authorName) {
        return ResponseEntity.ok(bookService.getBooksByAuthor(authorName));
    }

    @GetMapping("/by-title")
    @ApiOperation("Receiving List of Books with Specified Book's Title")
    public ResponseEntity<List<BookDto>> booksByTitle(@RequestParam("title") String title) {
        return ResponseEntity.ok(bookService.getBooksByTitle(title));
    }

    @GetMapping("/by-price-range")
    @ApiOperation("Receiving List of Books with Specified Range of Prices")
    public ResponseEntity<List<BookDto>> priceRangeBooks(@RequestParam("min") int min, @RequestParam("max") int max) {
        return ResponseEntity.ok(bookService.getBooksWithPriceBetween(min, max));
    }

    @GetMapping("/with-max-discount")
    @ApiOperation("Receiving List of Books with Maximum Discount Value")
    public ResponseEntity<List<BookDto>> maxDiscountBooks() {
        return ResponseEntity.ok(bookService.getBooksWithMaxDiscount());
    }

    @GetMapping("/bestsellers")
    @ApiOperation("Receiving List of Books which are Bestsellers (if field 'is_bestseller' has value = 1)")
    public ResponseEntity<List<BookDto>> bestsellerBooks() {
        return ResponseEntity.ok(bookService.getBestsellers());
    }

    @GetMapping("/recent")
    @ApiOperation("Receiving List of Books which were Published within the Last month " +
            "and Sorted by Date of publication descending." +
            "'offset' parameter is designed to set the first element of the list " +
            "and 'limit' parameter is helps to specify the number of elements to show")
    public ResponseEntity<List<BookDto>> recentBooks(@RequestParam("offset") Integer offset,
                                                     @RequestParam("limit") Integer limit) {
        return ResponseEntity.ok(bookService.getRecentBooks(offset, limit));
    }

    @GetMapping("/popular")
    @ApiOperation("Receiving List of Popular Books Sorted by Rating descending. " +
            "'offset' parameter is designed to set the first element of the list " +
            "and 'limit' parameter is helps to specify the number of elements to show")
    public ResponseEntity<List<BookDto>> popularBooks(@RequestParam(value = "offset") Integer offset,
                                                      @RequestParam(value = "limit") Integer limit) {
        return ResponseEntity.ok(bookService.getPopularBooks(offset, limit));
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
            "'offset' parameter is designed to set the first element of the list " +
            "and 'limit' parameter is helps to specify the number of elements to show")
    public ResponseEntity<List<BookDto>> taggedBooks(
            @PathVariable("tagId") Integer tagId,
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit
    ) {
        return ResponseEntity.ok(bookService.getPageOfBooksByTag(tagId, offset, limit).getContent());
    }
}
