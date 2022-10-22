package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.exceptions.EmptySearchQueryException;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.CountedBooksDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.HeaderInfoDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchPageController extends HeaderController {

    @Autowired
    public SearchPageController(UserRegistrationService userRegistrationService, BookService bookService) {
        super(userRegistrationService, bookService);
    }

    @ModelAttribute("searchResults")
    public List<BookDto> searchResults() {
        return new ArrayList<>();
    }

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String getSearchResults(@PathVariable(value = "searchWord", required = false) HeaderInfoDto headerInfoDto,
                                   Model model) throws EmptySearchQueryException {
        if (headerInfoDto == null) {
            throw new EmptySearchQueryException("Search with null query parameter is Impossible");
        }
        model.addAttribute("headerInfoDto", headerInfoDto);
        model.addAttribute("searchResults",
                getBookService().getPageOfSearchResultBooks(headerInfoDto.getSearchQuery(), 0, 5).getContent());
        model.addAttribute("searchResultsSize",
                getBookService().getSearchResultsSize(headerInfoDto.getSearchQuery()));
        return "/search/index";
    }

    @GetMapping("/search/page/{searchWord}")
    @ResponseBody
    public CountedBooksDto getNextSearchPage(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit,
            @PathVariable(value = "searchWord", required = false) HeaderInfoDto headerInfoDto
    ) {
        return new CountedBooksDto(
                getBookService().getPageOfSearchResultBooks(headerInfoDto.getSearchQuery(), offset, limit).getContent()
        );
    }
}
