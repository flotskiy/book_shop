package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.AuthorDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.CountedBooksDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.author.AuthorEntity;
import com.github.flotskiy.FlotskiyBookShopApp.service.AuthorService;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@PropertySource("application-variables.properties")
public class AuthorsPageController extends HeaderController {

    @Value("${initial.offset}")
    private int offset;

    @Value("${page.limit}")
    private int limit;

    @Value("${author.card.limit}")
    private int cardLimit;

    private final AuthorService authorService;

    @Autowired
    public AuthorsPageController(
            UserRegistrationService userRegistrationService,
            AuthorService authorService,
            BookService bookService) {
        super(userRegistrationService, bookService);
        this.authorService = authorService;
    }

    @GetMapping("/authors")
    public String authorsPage(Model model) {
        model.addAttribute("authorsMap", authorService.getAuthorsGroupedMap());
        return "/authors/index";
    }

    @GetMapping("/authors/{authorSlug}")
    public String author(@PathVariable("authorSlug") String authorSlug, Model model) {
        AuthorDto authorDto = authorService.getAuthorBySlug(authorSlug);
        int userId = getUserRegistrationService().getCurrentUserIdIncludingGuest();
        Page<BookDto> authorBooks = getBookService().getPageOfBooksByAuthorId(authorDto.getId(), offset, cardLimit, userId);
        model.addAttribute("author", authorDto);
        model.addAttribute("authorBooks", authorBooks);
        return "/authors/slug";
    }

    @GetMapping("/books/authors/{authorSlug}")
    public String getAuthorBooksPage(@PathVariable("authorSlug") String authorSlug, Model model) {
        AuthorDto authorDto = authorService.getAuthorBySlug(authorSlug);
        int userId = getUserRegistrationService().getCurrentUserIdIncludingGuest();
        Page<BookDto> authorBooks = getBookService().getPageOfBooksByAuthorId(authorDto.getId(), offset, limit, userId);
        model.addAttribute("authorObj", authorDto);
        model.addAttribute("authorBooksAll", authorBooks);
        return "/books/author";
    }

    @GetMapping("/books/author/{authorId}")
    @ResponseBody
    public CountedBooksDto getNextAuthorBooksPage(
            @PathVariable("authorId") Integer authorId,
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit
    ) {
        int userId = getUserRegistrationService().getCurrentUserIdIncludingGuest();
        return new CountedBooksDto(getBookService().getPageOfBooksByAuthorId(authorId, offset, limit, userId).getContent());
    }

    @GetMapping("/api/authors")
    @ResponseBody
    public List<AuthorEntity> authorsMapApi() {
        return authorService.getAuthorsEntity();
    }
}
