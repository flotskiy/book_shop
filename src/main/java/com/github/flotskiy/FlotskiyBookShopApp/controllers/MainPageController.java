package com.github.flotskiy.FlotskiyBookShopApp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bookshop")
public class MainPageController {

    @GetMapping("/main")
    public String mainPage() {
        return "index";
    }
}
