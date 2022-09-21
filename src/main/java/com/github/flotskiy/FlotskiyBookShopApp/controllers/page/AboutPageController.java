package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/about")
public class AboutPageController extends HeaderController {

    @GetMapping
    public String aboutPage() {
        return "/about";
    }
}
