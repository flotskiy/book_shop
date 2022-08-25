package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/genres")
public class GenresPageController {

    @GetMapping
    public String genresPage() {
        return "/genres/index";
    }
}
