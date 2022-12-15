package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.CountedBooksDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.GenreDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.service.GenreService;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@PropertySource("application-variables.properties")
public class GenresPageController extends HeaderController {

    @Value("${initial.offset}")
    private int offset;

    @Value("${page.limit}")
    private int limit;

    private final GenreService genreService;

    @Autowired
    public GenresPageController(
            UserRegistrationService userRegistrationService, GenreService genreService, BookService bookService
    ) {
        super(userRegistrationService, bookService);
        this.genreService = genreService;
    }

    @ModelAttribute("genresLinks")
    public List<GenreDto> genres() {
        return genreService.getAllGenresTree();
    }

    @GetMapping("/genres")
    public String genresPage() {
        return "/genres/index";
    }

    @GetMapping("/genres/{genreSlug}")
    public String genresPage(@PathVariable("genreSlug") String genreSlug, Model model) {
        GenreDto genreDto = genreService.findGenre(genreSlug, genreService.getGenreDtoList());
        Integer userId = getUserRegistrationService().getCurrentUserIdIncludingGuest();
        List<BookDto> bookDtos =
                getBookService().getPageOfBooksByGenreId(genreDto.getId(), offset, limit, userId).getContent();
        GenreDto parentDto = genreService.findParentGenre(genreDto);
        GenreDto rootDto = genreService.findParentGenre(parentDto);
        model.addAttribute("genreBookList", bookDtos);
        model.addAttribute("genreObject", genreDto);
        model.addAttribute("parentGenreObject", parentDto);
        model.addAttribute("rootGenreObject", rootDto);
        model.addAttribute("limitValue", limit);
        return "/genres/slug";
    }

    @GetMapping("/books/genre/{genreId}")
    @ResponseBody
    public CountedBooksDto getNextGenrePage(
            @PathVariable("genreId") Integer genreId,
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit
    ) {
        Integer userId = getUserRegistrationService().getCurrentUserIdIncludingGuest();
        return new CountedBooksDto(getBookService().getPageOfBooksByGenreId(genreId, offset, limit, userId).getContent());
    }
}
