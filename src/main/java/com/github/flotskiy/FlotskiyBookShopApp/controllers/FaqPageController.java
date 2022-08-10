package com.github.flotskiy.FlotskiyBookShopApp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/faq")
public class FaqPageController {

    @GetMapping
    public String faqPage() {
        return "/faq";
    }
}
