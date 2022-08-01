package com.github.flotskiy.FlotskiyBookShopApp.controllers;

import com.github.flotskiy.FlotskiyBookShopApp.data.AuthorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authors")
public class AuthorsPageController {

    private AuthorsService authorsService;

    @Autowired
    public AuthorsPageController(AuthorsService authorsService) {
        this.authorsService = authorsService;
    }

    @GetMapping
    public String authorsPage(Model model) {
        model.addAttribute("authorsData", authorsService.getAuthorsData());
        return "/authors/index";
    }
}
