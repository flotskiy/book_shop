package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.exceptions.ChangeBookStatusRedirectionException;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

@Controller
@RequestMapping("/books")
public class BookPageController extends HeaderController {

    private final UserBookService userBookService;
    private final ReviewAndLikeService reviewAndLikeService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final ResourceStorageService storage;
    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Autowired
    public BookPageController(
            UserRegistrationService userRegistrationService,
            BookService bookService,
            UserBookService userBookService,
            ReviewAndLikeService reviewAndLikeService,
            BooksRatingAndPopularityService booksRatingAndPopularityService,
            ResourceStorageService storage
    ) {
        super(userRegistrationService, bookService);
        this.userBookService = userBookService;
        this.reviewAndLikeService = reviewAndLikeService;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.storage = storage;
    }

    @GetMapping("/{slug}")
    public String bookPage(@PathVariable("slug") String slug, Model model) {
        Integer userId = getUserRegistrationService().getCurrentUserIdIncludingGuest();
        BookSlugDto bookSlugDto = getBookService().getBookSlugBySlug(slug, userId);
        boolean rateBookPossible =
                isAuthenticated() && booksRatingAndPopularityService.isRateBookPossible(bookSlugDto.getId(), userId);
        model.addAttribute("slugBook", bookSlugDto);
        model.addAttribute("idList", List.of(bookSlugDto.getId()));
        model.addAttribute("detailedRating",
                booksRatingAndPopularityService.getDetailedRatingDto(bookSlugDto.getId()));
        model.addAttribute("isRateBook", rateBookPossible);
        model.addAttribute("isAuthenticated", isAuthenticated());
        return "/books/slug";
    }

    @PostMapping("/{slug}/img/save")
    public String saveNewBookImage(@RequestParam("file") MultipartFile file, @PathVariable("slug") String slug)
            throws IOException {
        String savedPath = storage.saveNewBookImage(file, slug);
        BookEntity bookToUpdate = getBookService().getBookEntityBySlug(slug);
        bookToUpdate.setImage(savedPath);
        getBookService().saveBookEntity(bookToUpdate);
        return "redirect:/books/" + slug;
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> bookFile(@PathVariable("hash") String hash) throws IOException {
        Path path = storage.getBookFilePath(hash);
        logger.info("Downloading book file path is: " + path);

        MediaType mediaType = storage.getBookFileMime(hash);
        logger.info("Downloading book file mime type is: " + mediaType);

        byte[] data = storage.getBookFileByteArray(hash);
        logger.info("Downloading book file data length: " + data.length);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + path.getFileName().toString() + "\"")
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }

    @PostMapping("/changeBookStatus/")
    @ResponseBody
    public Map<String, Object> handleChangeBookStatus(@RequestBody BookStatusDto payload) {
        Map<String, Object> result = new HashMap<>();
        try {
            userBookService.changeBookStatus(payload.getBooksIds(), payload.getStatus());
            result.put("result", true);
        } catch (ChangeBookStatusRedirectionException changeBookStatusRedirectionException) {
            result.put("result", true);
        } catch (Throwable throwable) {
            result.put("result", false);
            result.put("error", "An error occurred, try again later");
        }
        return result;
    }

    @Secured("ROLE_USER")
    @PostMapping("/rateBook")
    @ResponseBody
    public Map<String, Object> rateBook(@RequestBody RateBookDto payload) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer userId = getUserRegistrationService().getCurrentUserIdIncludingGuest();
            booksRatingAndPopularityService
                    .setRatingToBookByUser(payload.getBookId(), userId, Integer.parseInt(payload.getValue()));
            result.put("result", true);
        } catch (Throwable throwable) {
            result.put("result", false);
            result.put("error", throwable.getMessage());
        }
        return result;
    }

    @Secured("ROLE_USER")
    @PostMapping("/rateBookReview")
    @ResponseBody
    public Map<String, Object> rateBookReview(@RequestBody RateBookReviewDto payload) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer userId = getUserRegistrationService().getCurrentUserIdIncludingGuest();
            booksRatingAndPopularityService.rateBookReview(payload.getReviewId(), userId, payload.getValue());
            result.put("result", true);
        } catch (Throwable throwable) {
            result.put("result", false);
            result.put("error", throwable.getMessage());
        }
        return result;
    }

    @Secured("ROLE_USER")
    @PostMapping("/bookReview")
    @ResponseBody
    public Map<String, Object> bookReview(@RequestBody BookReviewDto payload) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer userId = getUserRegistrationService().getCurrentUserIdIncludingGuest();
            reviewAndLikeService.bookReview(payload.getBookId(), userId, payload.getText());
            result.put("result", true);
        } catch (Throwable throwable) {
            result.put("result", false);
            result.put("error", throwable.getMessage());
        }
        return result;
    }
}
