package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.AuthorDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.CountedBooksDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.SearchWordDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.author.AuthorEntity;
import com.github.flotskiy.FlotskiyBookShopApp.service.AuthorService;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api()
public class AuthorsPageController {

    private final AuthorService authorService;
    private final BookService bookService;

    @Autowired
    public AuthorsPageController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @GetMapping("/authors")
    public String authorsPage(Model model) {
        model.addAttribute("authorsMap", authorService.getAuthorsGroupedMap());
        return "/authors/index";
    }

    @GetMapping("/authors/{authorSlug}")
    public String author(@PathVariable("authorSlug") String authorSlug, Model model) {
        AuthorDto authorDto = authorService.getAuthorBySlug(authorSlug);
        model.addAttribute("author", authorService.getAuthorBySlug(authorSlug));
        model.addAttribute("authorBooks", bookService.getPageOfBooksByAuthorId(authorDto.getId(), 0, 9));
        return "/authors/slug";
    }

    @GetMapping("/books/authors/{authorSlug}")
    public String getAuthorBooksPage(@PathVariable("authorSlug") String authorSlug, Model model) {
        AuthorDto authorDto = authorService.getAuthorBySlug(authorSlug);
        model.addAttribute("authorObj", authorDto);
        model.addAttribute(
                "authorBooksAll",
                bookService.getPageOfBooksByAuthorId(authorDto.getId(), 0, 20)
        );
        return "/books/author";
    }

    @GetMapping("/books/author/{authorId}")
    @ResponseBody
    public CountedBooksDto getNextAuthorBooksPage(
            @PathVariable("authorId") Integer authorId,
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit
    ) {

        return new CountedBooksDto(bookService.getPageOfBooksByAuthorId(authorId, offset, limit).getContent());
    }

    @ApiOperation("method to get map of authors")
    @GetMapping("/api/authors")
    @ResponseBody
    public List<AuthorEntity> authorsMapApi() {
        return authorService.getAuthorsEntity();
    }
}
