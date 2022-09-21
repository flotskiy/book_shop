package com.github.flotskiy.FlotskiyBookShopApp.controllers.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/signin")
public class SigninPageController extends HeaderController {

    @GetMapping
    public String signinPage() {
        return "/signin";
    }
}
