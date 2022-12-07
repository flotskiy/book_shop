package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.exceptions.EmptySearchQueryException;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.BookDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.book.CountedBooksDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.HeaderInfoDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.BookService;
import com.github.flotskiy.FlotskiyBookShopApp.security.UserRegistrationService;
import com.github.flotskiy.FlotskiyBookShopApp.service.GoogleSearchBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@PropertySource("application-variables.properties")
public class SearchPageController extends HeaderController {

    @Value("${initial.offset}")
    private int offset;

    @Value("${search.refresh.limit}")
    private int searchLimit;

    @Value("${page.limit}")
    private int limit;

    private final GoogleSearchBookService googleSearchBookService;

    @Autowired
    public SearchPageController(
            UserRegistrationService userRegistrationService,
            BookService bookService,
            GoogleSearchBookService googleSearchBookService
    ) {
        super(userRegistrationService, bookService);
        this.googleSearchBookService = googleSearchBookService;
    }

    @ModelAttribute("searchResults")
    public List<BookDto> searchResults() {
        return new ArrayList<>();
    }

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String getSearchResults(
            @PathVariable(value = "searchWord", required = false) HeaderInfoDto headerInfoDto,
            Model model
    ) throws EmptySearchQueryException {
        if (headerInfoDto == null) {
            throw new EmptySearchQueryException("Search with null query parameter is Impossible");
        }
        Integer userId = getUserRegistrationService().getCurrentUserId();
        model.addAttribute("headerInfoDto", headerInfoDto);
        List<BookDto> booksFound = getBookService()
                .getPageOfSearchResultBooks(headerInfoDto.getSearchQuery(), offset, searchLimit, userId).getContent();
        if (booksFound.size() == 0) {
            booksFound = googleSearchBookService
                    .getGoogleBooksApiSearchResult(headerInfoDto.getSearchQuery(), offset, limit);
        }
        model.addAttribute("searchResults", booksFound);
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
        Integer userId = getUserRegistrationService().getCurrentUserId();
        return new CountedBooksDto(
                getBookService().getPageOfSearchResultBooks(headerInfoDto.getSearchQuery(), offset, limit, userId).getContent()
        );
    }
}
