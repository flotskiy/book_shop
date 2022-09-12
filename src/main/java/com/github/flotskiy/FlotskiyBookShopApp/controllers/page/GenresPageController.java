package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.CountedBooksDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.GenreDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.SearchWordDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.genre.GenreEntity;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class GenresPageController {

    private final GenreService genreService;
    private final BookService bookService;

    @Autowired
    public GenresPageController(GenreService genreService, BookService bookService) {
        this.genreService = genreService;
        this.bookService = bookService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
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
        GenreEntity genreEntity = genreService.findGenreIdBySlug(genreSlug);
        List<BookDto> bookDtos = bookService.getPageOfBooksByGenreId(genreEntity.getId(), 0, 20).getContent();
        GenreEntity parentEntity = genreService.getParentEntity(genreEntity);
        GenreEntity rootEntity = genreService.getParentEntity(parentEntity);
        model.addAttribute("genreBookList", bookDtos);
        model.addAttribute("genreObject", genreEntity);
        model.addAttribute("parentGenreObject", parentEntity);
        model.addAttribute("rootGenreObject", rootEntity);
        return "/genres/slug";
    }

    @GetMapping("/books/genre/{genreId}")
    @ResponseBody
    public CountedBooksDto getNextGenrePage(
            @PathVariable("genreId") Integer genreId,
            @RequestParam("offset") Integer offset,
            @RequestParam("limit") Integer limit
    ) {
        return new CountedBooksDto(bookService.getPageOfBooksByGenreId(genreId, offset, limit).getContent());
    }
}
