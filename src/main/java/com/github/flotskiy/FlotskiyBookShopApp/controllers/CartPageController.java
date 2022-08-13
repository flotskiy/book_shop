package com.github.flotskiy.FlotskiyBookShopApp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
public class CartPageController {

    @GetMapping
    public String cartPage() {
        return "/cart";
    }
}
