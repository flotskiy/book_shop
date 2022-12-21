package com.github.flotskiy.bookshop.controllers.page;

import com.github.flotskiy.bookshop.exceptions.EmptySearchQueryException;
import com.github.flotskiy.bookshop.model.dto.book.BookDto;
import com.github.flotskiy.bookshop.model.dto.book.CountedBooksDto;
import com.github.flotskiy.bookshop.model.dto.HeaderInfoDto;
import com.github.flotskiy.bookshop.service.BookService;
import com.github.flotskiy.bookshop.security.UserRegistrationService;
import com.github.flotskiy.bookshop.service.GoogleSearchBookService;
import com.github.flotskiy.bookshop.util.CustomStringHandler;
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

    @Value("${page.limit}")
    private int limit;

    private final GoogleSearchBookService googleSearchBookService;
    private final CustomStringHandler customStringHandler;

    @Autowired
    public SearchPageController(
            UserRegistrationService userRegistrationService,
            BookService bookService,
            GoogleSearchBookService googleSearchBookService,
            CustomStringHandler customStringHandler) {
        super(userRegistrationService, bookService);
        this.googleSearchBookService = googleSearchBookService;
        this.customStringHandler = customStringHandler;
    }

    @ModelAttribute("searchResults")
    public List<BookDto> searchResults() {
        return new ArrayList<>();
    }

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String getSearchResults(@PathVariable(value = "searchWord", required = false) String searchWord, Model model)
            throws EmptySearchQueryException {
        if (searchWord == null && model.getAttribute("searchError") == null) {
            throw new EmptySearchQueryException("Search with null query parameter is Impossible");
        }
        if (model.getAttribute("searchError") == null) {
            HeaderInfoDto headerInfoDto = headerInfoDto();
            headerInfoDto.setSearchQuery(searchWord);
            Integer userId = getUserRegistrationService().getCurrentUserIdIncludingGuest();
            List<BookDto> booksFound = getBookService()
                    .getPageOfSearchResultBooks(searchWord, offset, limit, userId).getContent();
            if (booksFound.isEmpty()) {
                booksFound = googleSearchBookService
                        .getGoogleBooksApiSearchResult(searchWord, offset, limit);
            }
            int searchResultSize = getBookService().getSearchResultsSize(searchWord);
            model.addAttribute("headerInfoDto", headerInfoDto);
            model.addAttribute("searchResults", booksFound);
            model.addAttribute("searchResultsSize", searchResultSize);
            model.addAttribute("searchResultMessage",
                    customStringHandler.getSearchBookCountMessage(searchResultSize));
        }
        return "/search/index";
    }

    @GetMapping("/search/page/{searchWord}")
    @ResponseBody
    public CountedBooksDto getNextSearchPage(
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit,
            @PathVariable(value = "searchWord", required = false) String searchWord
    ) {
        Integer userId = getUserRegistrationService().getCurrentUserIdIncludingGuest();
        return new CountedBooksDto(
                getBookService().getPageOfSearchResultBooks(searchWord, offset, limit, userId).getContent()
        );
    }
}
