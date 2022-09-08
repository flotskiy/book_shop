package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.AuthorDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.dto.SearchWordDto;
import com.github.flotskiy.FlotskiyBookShopApp.model.entity.author.AuthorEntity;
import com.github.flotskiy.FlotskiyBookShopApp.service.AuthorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@Api()
public class AuthorsPageController {

    private final AuthorService authorService;

    @Autowired
    public AuthorsPageController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @ModelAttribute("authorsMap")
    public Map<String, List<AuthorDto>> authorsMap() {
        return authorService.getAuthorsGroupedMap();
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @GetMapping("/authors")
    public String authorsPage() {
        return "/authors/index";
    }

    @ApiOperation("method to get map of authors")
    @GetMapping("/api/authors")
    @ResponseBody
    public List<AuthorEntity> authorsMapApi() {
        return authorService.getAuthorsEntity();
    }
}
