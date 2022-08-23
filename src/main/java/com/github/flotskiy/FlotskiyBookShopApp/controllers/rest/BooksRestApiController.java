package com.github.flotskiy.FlotskiyBookShopApp.controllers.rest;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Api(tags = "Book Data API")
@Tag(name = "Book Data API", description = "Controller performs API operations with Books")
public class BooksRestApiController {

    private final BookService bookService;

    @Autowired
    public BooksRestApiController(BookService bookService) {
        this.bookService = bookService;
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
}
