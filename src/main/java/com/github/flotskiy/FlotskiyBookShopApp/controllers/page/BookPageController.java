package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.SearchWordDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.book.BookEntity;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.service.ResourceStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/books")
public class BookPageController {

    private final BookService bookService;
    private final ResourceStorage storage;

    @Autowired
    public BookPageController(BookService bookService, ResourceStorage storage) {
        this.bookService = bookService;
        this.storage = storage;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
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
}
