package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.exceptions.BookReviewException;
import com.github.flotskiy.FlotskiyBookShopApp.exceptions.RateBookByUserException;
import com.github.flotskiy.FlotskiyBookShopApp.exceptions.RateBookReviewException;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.page.BookSlugDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.post.BookReviewDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.post.BookStatusDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.post.RateBookReviewDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.post.RateBookDto;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

@Controller
@RequestMapping("/books")
public class BookPageController extends HeaderController {

    private final BookService bookService;
    private final ReviewAndLikeService reviewAndLikeService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final ResourceStorage storage;

    @Autowired
    public BookPageController(
            UserRegistrationService userRegistrationService,
            BookService bookService,
            ReviewAndLikeService reviewAndLikeService,
            BooksRatingAndPopularityService booksRatingAndPopularityService,
            ResourceStorage storage
    ) {
        super(userRegistrationService);
        this.bookService = bookService;
        this.reviewAndLikeService = reviewAndLikeService;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.storage = storage;
    }

    @GetMapping("/{slug}")
    public String bookPage(@PathVariable("slug") String slug, Model model) {
        BookSlugDto bookSlugDto = bookService.getBookSlugBySlug(slug);
        model.addAttribute("slugBook", bookSlugDto);
        model.addAttribute("detailedRating",
                booksRatingAndPopularityService.getDetailedRatingDto(bookSlugDto.getId()));
        return "/books/slug";
    }

    @PostMapping("/{slug}/img/save")
    public String saveNewBookImage(@RequestParam("file") MultipartFile file, @PathVariable("slug") String slug)
            throws IOException {
        String savedPath = storage.saveNewBookImage(file, slug);
        BookEntity bookToUpdate = bookService.getBookEntityBySlug(slug);
        bookToUpdate.setImage(savedPath);
        bookService.saveBookEntity(bookToUpdate);
        return "redirect:/books/" + slug;
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> bookFile(@PathVariable("hash") String hash) throws IOException {
        Path path = storage.getBookFilePath(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("Downloading book file path is: " + path);

        MediaType mediaType = storage.getBookFileMime(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("Downloading book file mime type is: " + mediaType);

        byte[] data = storage.getBookFileByteArray(hash);
        Logger.getLogger(this.getClass().getSimpleName()).info("Downloading book file data length: " + data.length);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + path.getFileName().toString() + "\"")
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }

    @PostMapping("/changeBookStatus/{slug}")
    public String handleChangeBookStatus(
            @PathVariable(value = "slug") String slug,
            @RequestBody BookStatusDto payload,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        bookService.changeBookStatus(slug, payload.getStatus(), request, response, model);
        if (slug.split(",").length == 1) {
            return "redirect:/books/" + slug;
        }
        return "redirect:/books/postponed";
    }

    @PostMapping("/rateBook")
    public String rateBook(@RequestBody RateBookDto payload) throws RateBookByUserException {
        Integer userId = 1; // Current user ID
        String slug = booksRatingAndPopularityService
                .setRatingToBookByUser(payload.getBookId(), userId, Integer.parseInt(payload.getValue()));
        return "redirect:/books/" + slug;
    }

    @PostMapping("/rateBookReview")
    public String rateBookReview(@RequestBody RateBookReviewDto payload) throws RateBookReviewException {
        Integer userId = 1; // Current user ID
        String slug = booksRatingAndPopularityService
                .rateBookReview(payload.getReviewId(), userId, payload.getValue());
        return "redirect:/books/" + slug;
    }

    @PostMapping("/bookReview")
    public String bookReview(@RequestBody BookReviewDto payload) throws BookReviewException {
        Integer userId = 1; // Current user ID
        String slug = reviewAndLikeService.bookReview(payload.getBookId(), userId, payload.getText());
        return "redirect:/books/" + slug;
    }
}
