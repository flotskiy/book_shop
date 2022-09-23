package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.service.CookieService;
import com.github.flotskiy.FlotskiyBookShopApp.service.ResourceStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
public class BookPageController extends HeaderController {

    private final BookService bookService;
    private final CookieService cookieService;
    private final ResourceStorage storage;

    @Autowired
    public BookPageController(BookService bookService, CookieService cookieService, ResourceStorage storage) {
        this.bookService = bookService;
        this.cookieService = cookieService;
        this.storage = storage;
    }

    @GetMapping("/{slug}")
    public String bookPage(@PathVariable("slug") String slug, Model model) {
        model.addAttribute("slugBook", bookService.getBookSlugBySlug(slug));
        return "/books/slug";
    }

    @PostMapping("/{slug}/img/save")
    public String saveNewBookImage(@RequestParam("file") MultipartFile file, @PathVariable("slug") String slug) throws IOException {
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
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName().toString() + "\"")
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }

    @PostMapping("/changeBookStatus/{slug}")
    public String handleChangeBookStatus(
            @PathVariable(value = "slug") String slug,
            @RequestParam("status") String status,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        String[] slugs = slug.split(",");
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
        if (slugs.length == 1) {
            return "redirect:/books/" + slug;
        }
        return "redirect:/books/postponed";
    }
}
