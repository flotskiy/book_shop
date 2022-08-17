package com.github.flotskiy.FlotskiyBookShopApp.controllers;

import com.github.flotskiy.FlotskiyBookShopApp.model.dto.AuthorDto;
import com.github.flotskiy.FlotskiyBookShopApp.service.AuthorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Map;

@Controller
public class AuthorsPageController {

    private final AuthorsService authorsService;

    @Autowired
    public AuthorsPageController(AuthorsService authorsService) {
        this.authorsService = authorsService;
    }

    @ModelAttribute("authorsMap")
    public Map<String, List<AuthorDto>> authorsMap() {
        return authorsService.getAuthorsGroupedMap();
    }

    @GetMapping("/authors")
    public String authorsPage() {
        return "/authors/index";
    }
}
